# Copyright (c) Facebook, Inc. and its affiliates. All Rights Reserved.

import argparse
import itertools
import logging

import pytorch_lightning
import torchmetrics
from torch.hub import load_state_dict_from_url
from torch.optim.lr_scheduler import StepLR

from pytorchvideo.data.hagrid import GestureDataset
import pytorchvideo.models.accelerator.mobile_cpu.efficient_x3d as efficient_x3d
import pytorchvideo.models.hub.efficient_x3d_mobile_cpu as hub_efficient_x3d
import torch
import torch.nn.functional as F
from pytorchvideo.losses.cosine_annealing_with_warmup import CosineAnnealingWarmUpRestarts
from pytorch_lightning.callbacks import (
    LearningRateMonitor,
    RichProgressBar,
    EarlyStopping,
    ModelCheckpoint,
    RichModelSummary,
    BackboneFinetuning
)
from pytorchvideo.transforms import (
    ApplyTransformToKey,
    Normalize,
    RandomShortSideScale,
    RemoveKey,
    ShortSideScale,
    UniformTemporalSubsample,
)
from slurm import copy_and_run_with_config
from torchvision.transforms import (
    CenterCrop,
    Compose,
    RandomCrop,
    RandomHorizontalFlip,
)


"""
This video classification example demonstrates how PyTorchVideo models, datasets and
transforms can be used with PyTorch Lightning module. Specifically it shows how a
simple pipeline to train a Resnet on the Kinetics video dataset can be built.

Don't worry if you don't have PyTorch Lightning experience. We'll provide an explanation
of how the PyTorch Lightning module works to accompany the example.

The code can be separated into three main components:
1. VideoClassificationLightningModule (pytorch_lightning.LightningModule), this defines:
    - how the model is constructed,
    - the inner train or validation loop (i.e. computing loss/metrics from a minibatch)
    - optimizer configuration

2. KineticsDataModule (pytorch_lightning.LightningDataModule), this defines:
    - how to fetch/prepare the dataset
    - the train and val dataloaders for the associated dataset

3. pytorch_lightning.Trainer, this is a concrete PyTorch Lightning class that provides
  the training pipeline configuration and a fit(<lightning_module>, <data_module>)
  function to start the training/validation loop.

All three components are combined in the train() function. We'll explain the rest of the
details inline.
"""


class VideoClassificationLightningModule(pytorch_lightning.LightningModule):
    def __init__(self, args):
        """
        This LightningModule implementation constructs a PyTorchVideo ResNet,
        defines the train and val loss to be trained with (cross_entropy), and
        configures the optimizer.
        """
        self.args = args
        super().__init__()
        self.train_accuracy = torchmetrics.Accuracy()
        self.val_accuracy = torchmetrics.Accuracy()

        #############
        # PTV Model #
        #############

        # Here we construct the PyTorchVideo model. For this example we're using a
        # ResNet that works with Kinetics (e.g. 400 num_classes). For your application,
        # this could be changed to any other PyTorchVideo model (e.g. for SlowFast use
        # create_slowfast).
        # num_classes=19 : depends on gesture dataset
        if self.args.arch == "video_x3d":
            print("Using EfficientX3d_S")
            self.model = efficient_x3d.create_x3d(
                num_classes=19,
                expansion='S',
                head_act='identity'
            )
            _state_dict = load_state_dict_from_url(
                hub_efficient_x3d._checkpoint_paths["efficient_x3d_s"],
                progress=True,
                map_location="gpu",
            )
            print("Pretrained weights loaded")

            #  Due to the classes mismatch between the pretrained model and the
            #  dataset, we need to remove the last layer of the pretrained model.
            del _state_dict["projection.model.weight"]
            del _state_dict["projection.model.bias"]
            self.model.load_state_dict(_state_dict, strict=False)

            # At first time, freeze all layers except the last layer
            for name, p in self.model.named_parameters():
                if 'projection.model' in name:
                    p.requires_grad = True
                else:
                    p.requires_grad = False
            print("Froze all layers except the last layer")

            self.batch_key = "video"
        else:
            raise Exception("{self.args.arch} not supported")

    def on_train_epoch_start(self):
        """
        For distributed training we need to set the datasets video sampler epoch so
        that shuffling is done correctly
        """
        epoch = self.trainer.current_epoch
        # if self.trainer.use_ddp:
        #     self.trainer.datamodule.train_dataset.dataset.video_sampler.set_epoch(epoch)

    def forward(self, x):
        """
        Forward defines the prediction/inference actions.
        """
        return self.model(x)

    def training_step(self, batch, batch_idx):
        """
        This function is called in the inner loop of the training epoch. It must
        return a loss that is used for loss.backwards() internally. The self.log(...)
        function can be used to log any training metrics.

        PyTorchVideo batches are dictionaries containing each modality or metadata of
        the batch collated video clips. Kinetics contains the following notable keys:
           {
               'video': <video_tensor>,
               'audio': <audio_tensor>,
               'label': <action_label>,
           }

        - "video" is a Tensor of shape (batch, channels, time, height, Width)
        - "audio" is a Tensor of shape (batch, channels, time, 1, frequency)
        - "label" is a Tensor of shape (batch, 1)

        The PyTorchVideo models and transforms expect the same input shapes and
        dictionary structure making this function just a matter of unwrapping the dict and
        feeding it through the model/loss.
        """
        x = batch[self.batch_key]
        y_hat = self.model(x)
        loss = F.cross_entropy(y_hat, batch["label"])
        acc = self.train_accuracy(F.softmax(y_hat, dim=-1), batch["label"])
        self.log("train_loss", loss)
        self.log(
            "train_acc", acc, on_step=True, on_epoch=True, prog_bar=True, sync_dist=True
        )
        # if (batch_idx + 1) % 7000 == 0:
        #     scheduler = self.lr_schedulers()
        #     scheduler.step()
        return loss

    def validation_step(self, batch, batch_idx):
        """
        This function is called in the inner loop of the evaluation cycle. For this
        simple example it's mostly the same as the training loop but with a different
        metric name.
        """
        x = batch[self.batch_key]
        y_hat = self.model(x)
        loss = F.cross_entropy(y_hat, batch["label"])
        acc = self.val_accuracy(F.softmax(y_hat, dim=-1), batch["label"])
        self.log("val_loss", loss)
        self.log(
            "val_acc", acc, on_step=True, on_epoch=True, prog_bar=True, sync_dist=True
        )
        return loss

    def configure_optimizers(self):
        """
        We use the SGD optimizer with per step cosine annealing scheduler.
        """
        # optimizer = torch.optim.Adam(
        #     self.parameters(),
        #     lr=self.args.lr,
        #     betas=self.args.betas,
        #     weight_decay=self.args.weight_decay,
        # )
        parameters = []
        for p in self.parameters():
            if p.requires_grad:
                parameters.append(p)

        optimizer = torch.optim.SGD(
            parameters,
            lr=self.args.lr,
            momentum=self.args.momentum,
            weight_decay=self.args.weight_decay,
        )
        scheduler = StepLR(optimizer, step_size=1, gamma=0.8)
        return [optimizer], [{"scheduler": scheduler, "interval": "step", "frequency": 6000}]


class HagridDataModule(pytorch_lightning.LightningDataModule):
    """
    This LightningDataModule implementation constructs a PyTorchVideo Kinetics dataset for both
    the train and val partitions. It defines each partition's augmentation and
    preprocessing transforms and configures the PyTorch DataLoaders.
    """

    def __init__(self, args):
        self.args = args
        super().__init__()

    def _make_transforms(self, mode: str):
        """
        ##################
        # PTV Transforms #
        ##################

        # Each PyTorchVideo dataset has a "transform" arg. This arg takes a
        # Callable[[Dict], Any], and is used on the output Dict of the dataset to
        # define any application specific processing or augmentation. Transforms can
        # either be implemented by the user application or reused from any library
        # that's domain specific to the modality. E.g. for video we recommend using
        # TorchVision, for audio we recommend TorchAudio.
        #
        # To improve interoperation between domain transform libraries, PyTorchVideo
        # provides a dictionary transform API that provides:
        #   - ApplyTransformToKey(key, transform) - applies a transform to specific modality
        #   - RemoveKey(key) - remove a specific modality from the clip
        #
        # In the case that the recommended libraries don't provide transforms that
        # are common enough for PyTorchVideo use cases, PyTorchVideo will provide them in
        # the same structure as the recommended library. E.g. TorchVision didn't
        # have a RandomShortSideScale video transform, so it's been added to PyTorchVideo.
        """
        if self.args.data_type == "video":
            transform = [
                self._video_transform(mode),
                RemoveKey("audio"),
            ]
        else:
            raise Exception(f"{self.args.data_type} not supported")

        return Compose(transform)

    def _video_transform(self, mode: str):
        """
        This function contains example transforms using both PyTorchVideo and TorchVision
        in the same Callable. For 'train' mode, we use augmentations (prepended with
        'Random'), for 'val' mode we use the respective deterministic function.
        """
        args = self.args
        return ApplyTransformToKey(
            key="video",
            transform=Compose(
                [
                    UniformTemporalSubsample(args.video_num_subsampled),
                    Normalize(args.video_means, args.video_stds),
                ]
                + (
                    [
                        RandomShortSideScale(
                            min_size=args.video_min_short_side_scale,
                            max_size=args.video_max_short_side_scale,
                        ),
                        RandomCrop(args.video_crop_size),
                        RandomHorizontalFlip(p=args.video_horizontal_flip_p),
                    ]
                    if mode == "train"
                    else [
                        ShortSideScale(args.video_min_short_side_scale),
                        CenterCrop(args.video_crop_size),
                    ]
                )
            ),
        )

    def train_dataloader(self):
        """
        Defines the train DataLoader that the PyTorch Lightning Trainer trains/tests with.
        """
        # sampler = DistributedSampler if self.trainer.use_ddp else RandomSampler
        train_transform = self._make_transforms(mode="train")
        self.train_dataset = GestureDataset(
            path_images=self.args.path_images,
            path_annotation=self.args.path_annotation,
            is_train=True,
            transform=train_transform,
        )
        return torch.utils.data.DataLoader(
            self.train_dataset,
            batch_size=self.args.batch_size,
            num_workers=self.args.workers,
            shuffle=True,
        )

    def val_dataloader(self):
        """
        Defines the train DataLoader that the PyTorch Lightning Trainer trains/tests with.
        """
        # sampler = DistributedSampler if self.trainer.use_ddp else RandomSampler
        val_transform = self._make_transforms(mode="val")
        self.val_dataset = GestureDataset(
            path_images=self.args.path_images,
            path_annotation=self.args.path_annotation,
            is_train=False,
            transform=val_transform,
        )
        return torch.utils.data.DataLoader(
            self.val_dataset,
            batch_size=self.args.batch_size,
            num_workers=self.args.workers,
            shuffle=False,
        )


def main():
    """
    To train the ResNet with the Kinetics dataset we construct the two modules above,
    and pass them to the fit function of a pytorch_lightning.Trainer.

    This example can be run either locally (with default parameters) or on a Slurm
    cluster. To run on a Slurm cluster provide the --on_cluster argument.
    """
    setup_logger()

    pytorch_lightning.trainer.seed_everything()
    parser = argparse.ArgumentParser(description="Train EfficientX3d on Hagrid dataset.")

    #  Cluster parameters.
    parser.add_argument("--on_cluster", action="store_true")
    parser.add_argument("--job_name", default="ptv_video_classification", type=str)
    parser.add_argument("--working_directory", default=".", type=str)
    parser.add_argument("--partition", default="dev", type=str)

    # Model parameters.
    parser.add_argument("--lr", "--learning-rate", default=2e-05, type=float)
    parser.add_argument("--momentum", default=0.9, type=float)
    parser.add_argument("--betas", default=(0.9,0.999), type=tuple)
    parser.add_argument("--weight_decay", default=1e-4, type=float)
    parser.add_argument(
        "--arch",
        default="video_x3d",
        choices=["video_x3d"],
        type=str,
    )

    # Data parameters.
    parser.add_argument("--path_images",
                        default='/home/thisiswooyeol/Downloads/hagrid_dataset_512/subsample',
                        type=str, required=False)  # temporarily changed: required=True -> False
    parser.add_argument("--path_annotation",
                        default='/home/thisiswooyeol/Downloads/hagrid_dataset_512/annotations/ann_subsample',
                        type=str, required=False)  # temporarily changed: required=True -> False
    parser.add_argument("--workers", default=10, type=int)
    parser.add_argument("--batch_size", default=16, type=int)
    parser.add_argument("--clip_duration", default=2, type=float)
    parser.add_argument(
        "--data_type", default="video", choices=["video"], type=str
    )
    parser.add_argument("--video_num_subsampled", default=4, type=int)
    parser.add_argument("--video_means", default=(0.45, 0.45, 0.45), type=tuple)
    parser.add_argument("--video_stds", default=(0.225, 0.225, 0.225), type=tuple)
    parser.add_argument("--video_crop_size", default=224, type=int)
    parser.add_argument("--video_min_short_side_scale", default=256, type=int)
    parser.add_argument("--video_max_short_side_scale", default=320, type=int)
    parser.add_argument("--video_horizontal_flip_p", default=0.5, type=float)

    # Trainer parameters.
    parser = pytorch_lightning.Trainer.add_argparse_args(parser)
    parser.set_defaults(
        accelerator='gpu',
        devices=1,
        max_epochs=10,
        callbacks=[
            EarlyStopping('val_loss'),
            # ModelCheckpoint(dirpath=)
            LearningRateMonitor(),
            RichModelSummary(),
            RichProgressBar(leave=True),
        ],
        replace_sampler_ddp=False,
    )

    # Build trainer, ResNet lightning-module and Kinetics data-module.
    args = parser.parse_args()

    if args.on_cluster:
        copy_and_run_with_config(
            train,
            args,
            args.working_directory,
            job_name=args.job_name,
            time="72:00:00",
            partition=args.partition,
            gpus_per_node=args.gpus,
            ntasks_per_node=args.gpus,
            cpus_per_task=10,
            mem="470GB",
            nodes=args.num_nodes,
            constraint="volta32gb",
        )
    else:  # local
        train(args)


def train(args):
    trainer = pytorch_lightning.Trainer.from_argparse_args(args)
    classification_module = VideoClassificationLightningModule(args)
    data_module = HagridDataModule(args)
    trainer.fit(classification_module, data_module)


def setup_logger():
    ch = logging.StreamHandler()
    formatter = logging.Formatter("\n%(asctime)s [%(levelname)s] %(name)s: %(message)s")
    ch.setFormatter(formatter)
    logger = logging.getLogger("pytorchvideo")
    logger.setLevel(logging.DEBUG)
    logger.addHandler(ch)


if __name__ == "__main__":
    main()
