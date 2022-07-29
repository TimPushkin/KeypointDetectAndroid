# FeatureDetectAndroid

Android app for comparing traditional and learned techniques of feature detection and description. It provides an interface for selecting a keypoints detection algorithm and displays an image with keypoints and their detection time.

Supported algorithms:
1. Traditional:
  - SIFT
  - SURF
  - ORB
2. Learned:
  - SuperPoint
  
App uses original library with 2 parts: the native one (based on OpenCV C++) and the JVM one (based on PyTorch for Kotlin).

<img src="https://imgbox.com/cqNjK4FN" alt "Hi"/>

## Getting started

To build this project you need an OpenCV build suitable for the required Android ABI in your system.

Run `BuildOpenCV.cmake` script which will download and build OpenCV
automatically with the recommended optimizations. Read more about how to run the script in the
native part's README. You have to run it once for every Android ABI you plan to use.

You also need installed pytorch. If you don't have it, call `pip install pytorch`.

Then do the following:
1. Call `git submodule update --init`.
2. Run `utils/save_for_mobile.py` with utils as the working directory.

After these steps select your active ABI and build the project with Gradle -- the app is available in `:app` module and the library is available in
`:lib` module.

Currently, required JVM classes, implementing native ones, are automatically generated.

## User interaction

The app is very easy to use. Give it permission to use the camera. Then you are free to analyze everything around you with it! To select a keypoint detection algorithm, open the menu with a swipe to the right. By default, the "None" mode is set, in which only the image from the camera is displayed on the screen. Select the desired algorithm, close the menu, point the camera at the object or view of interest and observe the obtained keypoints and their detection time.
<img src="https://imgbox.com/n4bj9Jw6" alt "Permission"/>
<img src="https://imgbox.com/NwBPkKMt" alt "Menu"/>
<img src="https://imgbox.com/0ATu3Kq8" alt "App's appereance"/>
