# KeypointDetectAndroid

Android app for comparing traditional and learned techniques of feature detection and description.

Available modes:

- **File analysis**: detect keypoints on an image file with detailed logging for statistical analysis
- **Camera analysis**: detect keypoints on camera snapshots in soft-realtime

Supported algorithms:

- **Traditional**: [SIFT](http://www.scholarpedia.org/article/Scale_Invariant_Feature_Transform),
  [SURF](https://link.springer.com/chapter/10.1007/11744023_32), [ORB](https://ieeexplore.ieee.org/document/6126544)
- **Learned**: [SuperPoint](https://arxiv.org/abs/1712.07629)

![App GUI](https://i.imgur.com/VzYtGSX.png)

### Library

App uses its original library consisting of two parts:

- Native (traditional algorithms as implemented in [OpenCV](https://opencv.org))
- JVM (learned algorithms based on [PyTorch](https://pytorch.org) for Android)

## Getting started

Follow these steps to build the app:

1. **Configure traditional algorithms**: run [BuildOpenCV](lib/src/main/cpp/BuildOpenCV.cmake) script
   to automatically download and install OpenCV for the required Android ABI. Read more about this script and its
   arguments in the [native README](lib/src/main/cpp/README.md).
2. **Configure learned algorithms**:

```shell
# In any of the the project directories
pip install pytorch          # Installs PyTorch
git submodule update --init  # Initializes SuperPoint Git submodule

# In `utils` subdirectory
python save_for_mobile.py    # Converts SuperPoint to mobile and saves it to library's assets
```

3. **Build the project** with Gradle for the required ABI (this must be the same ABI you built OpenCV for).

## App usage

The app is very easy to use in both of its modes, no configuration needed.

|                   Main menu                   |                   File analysis                   |                   Camera analysis                   |
|:---------------------------------------------:|:-------------------------------------------------:|:---------------------------------------------------:|
| ![Main menu](https://i.imgur.com/uqOe4yv.png) | ![File analysis](https://i.imgur.com/RRkq81W.png) | ![Camera analysis](https://i.imgur.com/1uz2DaF.png) |

### File analysis

- Select a keypoint detection algorithm in the bottom menu (by default, `None` is selected, in which no detection is
  performed)
- Pick an image to analyze in the bottom menu: `Files…` - `Pick image`
- (Optional) Pick a file to write logs into in the bottom menu: `Files…` - `Pick log`
- Press `Start` and enter the number of runs to perform
- Mean detection time and error (unbiased estimation of standard deviation) will be shown and updated after each run —
  these are reset after each new start

Logs have the following format:
`[algorithm] [image width]x[image height] [number of keypoints] [detection time in milliseconds]` for each detection run

### Camera analysis

Give the app camera permission, and you are free to analyze everything around you with it!

- Select a keypoint detection algorithm in the bottom menu (by default, `None` is selected, in which no detection is
  performed)
- (Optional) Select a camera resolution in the bottom menu
- Point the camera at the scene of interest, the detected keypoints and their detection time will be displayed
