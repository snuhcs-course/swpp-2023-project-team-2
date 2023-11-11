import os
from typing import Any, Callable, Dict, Optional, Type

import torch

import pytorchvideo.data
from pytorchvideo.data.clip_sampling import ClipSampler
from pytorchvideo.transforms import (
    ApplyTransformToKey,
    Normalize,
    RandomShortSideScale,
    RemoveKey,
    ShortSideScale,
    UniformTemporalSubsample,
)
from torchvision.transforms import (
    CenterCrop,
    Compose,
    RandomCrop,
    RandomHorizontalFlip,
)

from .labeled_video_dataset import labeled_video_dataset, LabeledVideoDataset

"""
    Dynamic Facial Expression Recognition (DFER) video dataset for FERV39k
    FERV39k: A Large-Scale Multi-Scene Dataset for Facial Expression Recognition in Videos
    <https://wangyanckxx.github.io/Proj_CVPR2022_FERV39k.html>
"""


def Ferv39k(
        data_path: str,
        clip_sampler: ClipSampler,
        video_sampler: Type[torch.utils.data.Sampler] = torch.utils.data.RandomSampler,
        transform: Optional[Callable[[Dict[str, Any]], Dict[str, Any]]] = None,
        video_path_prefix: str = "",
        decode_audio: bool = True,
        decoder: str = "pyav",
) -> LabeledVideoDataset:
    torch._C._log_api_usage_once("PYTORCHVIDEO.dataset.Ferv39k")

    return labeled_video_dataset(
        data_path,
        clip_sampler,
        video_sampler,
        transform,
        video_path_prefix,
        decode_audio,
        decoder,
    )


if __name__ == "__main__":

    data_path = "/home/thisiswooyeol/Downloads/"
    def _video_transform(mode: str):
        """
        This function contains example transforms using both PyTorchVideo and TorchVision
        in the same Callable. For 'train' mode, we use augmentations (prepended with
        'Random'), for 'val' mode we use the respective determinstic function.
        """
        return ApplyTransformToKey(
            key="video",
            transform=Compose(
                [
                    UniformTemporalSubsample(num_samples=13),
                    Normalize((0.45, 0.45, 0.45), (0.225, 0.225, 0.225)),
                ]
                + (
                    [
                        RandomShortSideScale(
                            min_size=180,
                            max_size=226,
                        ),
                        RandomCrop(160),
                        RandomHorizontalFlip(p=0.5),
                    ]
                    if mode == "train"
                    else [
                        ShortSideScale(180),
                        CenterCrop(160),
                    ]
                )
            ),
        )

    train_dataset = Ferv39k(
        data_path=os.path.join(data_path, "0_7_LabelClips_test"),
        clip_sampler=pytorchvideo.data.make_clip_sampler(
            "random", 2,
        ),
        transform=_video_transform(mode="train"),
    )

    train_dataloader = torch.utils.data.DataLoader(
        train_dataset,
        batch_size=32,
        num_workers=8,
    )

    for data in train_dataloader:
        print(data.keys())
        print(data['video'].shape)
        print(data['video_name'][0])
        print(data['video_index'])
        print(data['clip_index'])
        print(data['aug_index'])
        print(data['label'])
        break
