import os
import json
import random
from typing import Tuple, Union, Dict

import pandas as pd
from PIL import Image

import torch
from torch import Tensor
from torchvision import transforms as T

CLASS_NAMES = [
    'call',
    'dislike',
    'fist',
    'four',
    'like',
    'mute',
    'ok',
    'one',
    'palm',
    'peace_inverted',
    'peace',
    'rock',
    'stop_inverted',
    'stop',
    'three',
    'three2',
    'two_up',
    'two_up_inverted',
    'no_gesture']

FORMATS = (".jpeg", ".jpg", ".jp2", ".png", ".tiff", ".jfif", ".bmp", ".webp", ".heic")


class GestureDataset(torch.utils.data.Dataset):
    def __init__(
            self,
            path_annotation: str,
            path_images: str,
            is_train: bool,
            transform=None
    ) -> None:
        self.is_train = is_train
        self.transform = transform
        self.path_annotation = path_annotation
        self.path_images = path_images
        self.labels = {label: num for (label, num) in
                       zip(CLASS_NAMES, range(len(CLASS_NAMES)))}
        self.annotations = self._read_annotations(self.path_annotation)
        self._NUM_FRAMES = 4
        self._MAX_SIDE = 512

    def _read_annotations(self, path):
        annotations_all = None
        exists_images = []
        for target in CLASS_NAMES:
            path_to_csv = os.path.join(path, f"{target}.json")
            if os.path.exists(path_to_csv):
                json_annotation = json.load(open(
                    os.path.join(path, f"{target}.json")
                ))

                json_annotation = [dict(annotation, **{"name": f"{name}.jpg"}) for name, annotation in
                                   zip(json_annotation, json_annotation.values())]

                annotation = pd.DataFrame(json_annotation)

                annotation["target"] = target
                annotations_all = pd.concat([annotations_all, annotation], ignore_index=True)
                exists_images.extend(
                    self._get_files_from_dir(os.path.join(self.path_images, target), FORMATS))
            else:
                if target != 'no_gesture':
                    print(f"Database for {target} not found")

        annotations_all["exists"] = annotations_all["name"].isin(exists_images)

        annotations_all = annotations_all[annotations_all["exists"]]

        users = annotations_all["user_id"].unique()
        users = sorted(users)
        random.Random(42).shuffle(users)
        train_users = users[:int(len(users) * 0.8)]
        val_users = users[int(len(users) * 0.8):]

        annotations_all = annotations_all.copy()

        if self.is_train:
            annotations_all = annotations_all[annotations_all["user_id"].isin(train_users)]
        else:
            annotations_all = annotations_all[annotations_all["user_id"].isin(val_users)]

        return annotations_all

    def __len__(self):
        return self.annotations.shape[0]

    def _get_sample(self, index: int) -> Dict[str, Union[Tensor]]:
        """
        - "video" is a Tensor of shape (channels, time, height, Width)
        - "label" is a Tensor of shape (1)
        """
        # FIXME: Pad Image & Randomly shift image along time axis
        row = self.annotations.iloc[[index]].to_dict('records')[0]
        image_pth = os.path.join(self.path_images, row["target"], row["name"])
        image = Image.open(image_pth).convert("RGB")
        image = self._pad_image(image, _size=(self._MAX_SIDE, self._MAX_SIDE))
        # FIXME: find out suitable _num_frames
        video = self._image_to_video(image, _num_frames=self._NUM_FRAMES)

        labels = torch.LongTensor([self.labels[label] for label in row["labels"]
                                   if self.labels[label] != self.labels['no_gesture']]).item()

        sample_dict = {
            "video": video,
            "label": labels,
        }

        return sample_dict

    def __getitem__(self, index: int):
        sample_dict = self._get_sample(index)
        if self.transform is not None:
            sample_dict = self.transform(sample_dict)

        return sample_dict

    @staticmethod
    def _get_files_from_dir(pth: str, extns: Tuple):
        if not os.path.exists(pth):
            print(f"Dataset directory doesn't exist {pth}")
            return []
        files = [f for f in os.listdir(pth) if f.endswith(extns)]
        return files

    @staticmethod
    def _pad_image(image: Image.Image, _size: Tuple[int, int]):
        # convert PIL image  (H, W, C) -> (C, H, W)
        image = T.ToTensor()(image)
        padded_image = torch.zeros((3, *_size))
        padded_image[:, :image.shape[1], :image.shape[2]] = image
        return padded_image

    @staticmethod
    def _image_to_video(image: torch.Tensor, _num_frames: int):
        # TODO : make image animation
        video = torch.stack([image] * _num_frames, dim=1)
        return video


if __name__ == '__main__':
    # Define dataset
    train_dataset = GestureDataset(
        path_images='/home/thisiswooyeol/Downloads/hagrid_dataset_512/subsample',
        path_annotation='/home/thisiswooyeol/Downloads/hagrid_dataset_512/annotations/ann_subsample',
        is_train=True,
        transform=None,
    )
    test_dataset = GestureDataset(
        path_images='/home/thisiswooyeol/Downloads/hagrid_dataset_512/subsample',
        path_annotation='/home/thisiswooyeol/Downloads/hagrid_dataset_512/annotations/ann_subsample',
        is_train=False,
        transform=None,
    )

    # Define dataloader
    train_dataloader = torch.utils.data.DataLoader(
        train_dataset,
        batch_size=8,
        num_workers=4,
        shuffle=True,

    )
    test_dataloader = torch.utils.data.DataLoader(
        test_dataset,
        batch_size=8,
        num_workers=4,
        shuffle=True,
    )

    # Check dataloader
    for sample_dict in train_dataloader:
        print(type(sample_dict["video"]), sample_dict["video"].shape)
        print(sample_dict["label"])
        break
    for sample_dict in test_dataloader:
        print(type(sample_dict["video"]), sample_dict["video"].shape)
        print(sample_dict["label"])
        break
