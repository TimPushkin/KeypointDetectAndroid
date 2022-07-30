# FeatureDetectAndroid

Android app for comparing traditional and learned techniques of feature detection and description. It provides a
GUI for selecting a keypoint detection algorithm and displays an image with keypoints and their detection time in soft
real-time.

Supported algorithms:

|   Traditional   |  Learned   |
|:---------------:|:----------:|
| SIFT, SURF, ORB | SuperPoint |

![Detection result example](https://i.imgur.com/lD8ERK3.png)

App uses its original library consisting of parts: native (traditional algorithms from OpenCV) and JVM (learned
algorithms based on PyTorch for Android).

## Getting started

Follow these steps to buils the app:

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

The app is very easy to use: give it the camera permission, and you are free to analyze everything around you with it!

- To select a keypoint detection algorithm, open the menu with a swipe to the right.
- By default, the `None` mode is set, in which only the image from the camera is displayed on the screen.
- Select the desired algorithm, close the menu, and point the camera at the scene of interest and observe.
- The obtained keypoints and their detection time will be displayed on screen.

| ![Permission](https://i.imgur.com/dMPUeci.png) | ![Menu](https://i.imgur.com/HE9lq7D.png)  | ![Detection](https://i.imgur.com/TQbCCZu.png) |
|:----------------------------------------------:|:-----------------------------------------:|:---------------------------------------------:|
