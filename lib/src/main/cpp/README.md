# Traditional keypoint detection algorithms

This directory contains the native implementation of the SGBM-based depth estimation algorithm.

## Description

[SIFT  (Scale-Invariant Feature Transform)](https://docs.opencv.org/3.4/d7/d60/classcv_1_1SIFT.html)

[SURF (Speeded-Up Robust Features)](https://docs.opencv.org/3.4/d5/df7/classcv_1_1xfeatures2d_1_1SURF.html)

[ORB (Oriented FAST and Rotated BRIEF)](https://docs.opencv.org/3.4/d7/d60/classcv_1_1SIFT.html)

These algorithms from OpenCV is used to
calculate keypoints and descriptors from image. The result is:
Firsly, vector of structures which contains floating-point numbers representing each keypoint of picture
Secondly, a vector of descriptors accounting each keypoint which is also a vector of a floating-point numbers

clang opencv contrib llvm aim path by cc cxx or set clang as highest priority compiller


## Project structure

The project consists of the following directories:

- `examples` – usage examples (see below)
- `external` - 3rd-party libraries
- `include` – public headers to include when importing this CMake project
- `src` – source code

## Build

**Note**: when building on Windows, you may be required to specify a non-default generator, for
example, `ninja`, with `-DCMAKE_GENERATOR=<generator name>` in the configuration command, because
Windows' default generator `nmake` seems to fail.

This project requires [OpenCV](https://github.com/opencv/opencv) 4.6.0 and [OpenCV-contrib](https://github.com/opencv/opencv_contrib) contains extra modules, you should install same version as main libary. You can either
manually download and install them, or run `BuildOpenCV.cmake` script that will download and build
both of them for you for you.

Using opencv-contrib on intel architecture requires [clang-14](https://apt.llvm.org/) when you configure project with or without examples, it doesn't matters. Specify path in environment variables:
```shell
export CC=<path>
```
```shell
export CXX=<path>
```
Or specify clang as the highest priority compiler:
```shell
sudo update-alternatives --config c++
```
```shell
sudo update-alternatives --config cc
```
If you already have OpenCV, you can build this project with CMake:

```shell
# In this project's root directory (where the top level CMakeLists.txt is)
cmake .          # Configure the build
cmake --build .  # Start the build
```

### Running OpenCV build script

`BuildOpenCV.cmake` can download the required version of OpenCV and OpenCV-contrib and build it with the optimal
configuration. OpenCV will then reside in `build/opencv-build-<optional suffix>`, where this project
will search for it first.

The script can be run as follows:

```shell
cmake <options> -P BuildOpenCV.cmake
```

In most cases no options are needed, as they are going to be inferred from the system by CMake. But
if you plan to build the project for some other platform, you might need to specify the following
options in the form of `-D<variable>=<value>`:

- `BUILD_DIR_SUFFIX` -- suffix to append to OpenCV build directory name (defaults to none)
- `CMAKE_GENERATOR` -- a generator to use (for example, `Ninja`, defaults to system default)
- `CMAKE_TOOLCHAIN_FILE` -- path to a CMake toolchain file (defaults to none)
- `CMAKE_C_COMPILER` and `CMAKE_CXX_COMPILER` -- C/C++ compiler to use (for example, `clang`
  /`clang++` or a path to a compiler, defaults to system default)
- `ANDROID_ABI` -- if building for Android, for which ABI to build (should be one of
  the [supported ABIs](https://developer.android.com/ndk/guides/abis), defaults to the ABI from the
  selected toolchain if any)
- `ADD_ANDROID_ABI_CHECK` -- whether to modify `OpenCVConfig-version.cmake` with an additional check
  for Android ABI compatibility (`ON` or `OFF`, disabled by default)
- `ANDROID_ARM_NEON` -- if building for Android, whether to let OpenCV make use of Neon or not (`ON`
  or `OFF`, disabled by default)

The toolchain file specified can influence the other variables default values.

For example, if you want to build for Android with ABI `armeabi-v7a` and Neon support, having NDK
installed in the system, the command to run the script will be something like:

```shell
cmake -D CMAKE_TOOLCHAIN_FILE="<NDK path>/build/cmake/android.toolchain.cmake" \
      -D CMAKE_C_COMPILER="<NDK path>/toolchains/llvm/prebuilt/<platform>/bin/clang<extension>" \
      -D CMAKE_CXX_COMPILER="<NDK path>/toolchains/llvm/prebuilt/<platform>/bin/clang++<extension>" \
      -D ANDROID_ABI=armeabi-v7a \
      -D ANDROID_ARM_NEON=ON \
      -D ADD_ANDROID_ABI_CHECK=ON \
      -P BuildOpenCV.cmake
```

### Generating a language interface

**Note**: this requires `clang-14.0.0` or newer to be used for building both OpenCV and the project;
if you are building for Android, use NDK `r25` or newer.

To generate an interface for Java, Objective-C, Python, and some other languages modify the
configure command as follows:

```shell
cmake -DSCAPIX_BRIDGE=<language> .
```

[Scapix](https://github.com/scapix-com/scapix) is used for this -- see its page for the set of the
supported languages.

The interface will be placed in the corresponding subdirectory of `generated` directory after the
build.

## Usage examples

Files in `examples` directory demonstrate how to use this project for depth estimation. Specify
`-DBUILD_EXAMPLES=ON` to build them.

- `detection_exmaple.cpp` accepts one CLI arguments: path to the image, then calculates keypoints and assosiated descriptors with three algorithms and prints them separately to `stdout`.
