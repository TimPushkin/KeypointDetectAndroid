set(PROJECT_BUILD_DIR ${CMAKE_SOURCE_DIR}/build)

set(OPENCV_VERSION 4.6.0)
# TODO: remove unused modules
set(OPENCV_MODULES core,calib3d,imgproc,imgcodecs,features2d,xfeatures2d)

set(OPENCV_DOWNLOAD_DIR ${PROJECT_BUILD_DIR}/opencv-download)
set(OPENCV_BUILD_DIR ${PROJECT_BUILD_DIR}/opencv-build)

# Function which downloads and extracts OpenCV sources
function(get_opencv repo)
    if (NOT EXISTS ${OPENCV_DOWNLOAD_DIR}/${repo}-${OPENCV_VERSION}/LICENSE)
        message(STATUS "${repo} not extracted into ${OPENCV_DOWNLOAD_DIR}")

        if (NOT EXISTS ${OPENCV_DOWNLOAD_DIR}/${repo}-${OPENCV_VERSION}.zip)
            message(STATUS "${repo} not downloaded into ${OPENCV_DOWNLOAD_DIR}")

            message(STATUS "Downloading ${repo}")
            file(
                    DOWNLOAD
                    https://github.com/opencv/${repo}/archive/refs/tags/${OPENCV_VERSION}.zip
                    ${OPENCV_DOWNLOAD_DIR}/${repo}-${OPENCV_VERSION}.zip
                    SHOW_PROGRESS
            )
            message(STATUS "Downloading ${repo} - done")
        endif ()

        message(STATUS "Extracting ${repo}")
        file(
                ARCHIVE_EXTRACT
                INPUT ${OPENCV_DOWNLOAD_DIR}/${repo}-${OPENCV_VERSION}.zip
                DESTINATION ${OPENCV_DOWNLOAD_DIR}
        )
        message(STATUS "Extracting ${repo} - done")
    endif ()
endfunction()

# Append suffix to the build directory name
if (BUILD_DIR_SUFFIX)
    string(APPEND OPENCV_BUILD_DIR "-${BUILD_DIR_SUFFIX}")
endif ()

# Download and extract OpenCV sources
GET_OPENCV(opencv)

# Download and extract OpenCV-contrib sources
GET_OPENCV(opencv_contrib)

# Set toolchain-related flags
if (CMAKE_GENERATOR)
    list(APPEND TOOLCHAIN_ARGS -DCMAKE_GENERATOR=${CMAKE_GENERATOR})
endif ()
if (CMAKE_TOOLCHAIN_FILE)
    list(APPEND TOOLCHAIN_ARGS -DCMAKE_TOOLCHAIN_FILE=${CMAKE_TOOLCHAIN_FILE})
endif ()
if (CMAKE_C_COMPILER)
    list(APPEND TOOLCHAIN_ARGS -DCMAKE_C_COMPILER=${CMAKE_C_COMPILER})
endif ()
if (CMAKE_CXX_COMPILER)
    list(APPEND TOOLCHAIN_ARGS -DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER})
endif ()

# Set Android-related flags
if (ANDROID_ABI)
    list(APPEND ANDROID_ARGS -DANDROID_ABI=${ANDROID_ABI})
endif ()
if (ANDROID_ARM_NEON)
    list(APPEND ANDROID_ARGS -DANDROID_ARM_NEON=${ANDROID_ARM_NEON})
endif ()

# Configure OpenCV build
set(
        OPENCV_CMAKE_ARGS  # https://docs.opencv.org/4.6.0/db/d05/tutorial_config_reference.html
        # General
        -DBUILD_LIST=${OPENCV_MODULES}
        -DOPENCV_EXTRA_MODULES_PATH=${OPENCV_DOWNLOAD_DIR}/opencv_contrib-${OPENCV_VERSION}/modules
        -DOPENCV_ENABLE_NONFREE=ON
        # Bundled components              TODO: disable the unwanted dependencies(1)
        -DWITH_GTK=OFF
        -DWITH_WIN32UI=OFF
        -DBUILD_TESTS=OFF
        -DBUILD_PERF_TESTS=OFF
        -DBUILD_ANDROID_PROJECTS=OFF
        -DBUILD_ANDROID_EXAMPLES=OFF
        -DBUILD_opencv_apps=OFF
        -DBUILD_JAVA=OFF
        -DBUILD_FAT_JAVA_LIB=OFF
        -DBUILD_KOTLIN_EXTENSIONS=OFF
        -DBUILD_OBJC=OFF
        -DBUILD_opencv_python2=OFF
        -DBUILD_opencv_python3=OFF
        # Disabled dependencies            TODO: enable  dependencies which will be needed
        #        -DWITH_1394=OFF
        #        -DWITH_AVFOUNDATION=OFF
        #        -DWITH_CAP_IOS=OFF
        #        -DWITH_VTK=OFF
        #        -DWITH_GSTREAMER=OFF
        #        -DWITH_GTK=OFF
        #        -DWITH_WIN32UI=OFF
        #        -DWITH_FFMPEG=OFF
        #        -DWITH_V4L=OFF
        #        -DWITH_DSHOW=OFF
        #        -DWITH_MSMF=OFF
        #        -DWITH_DIRECTX=OFF
        #        -DWITH_VA=OFF
        #        -DWITH_VA_INTEL=OFF
        #        -DWITH_LAPACK=OFF
        #        -DWITH_PROTOBUF=OFF
        #        -DWITH_QUIRC=OFF
        #        -DWITH_ANDROID_MEDIANDK=OFF
        #        -DWITH_ANDROID_NATIVE_CAMERA=OFF
        # Enabled dependencies             # TODO: disable the unwanted dependencies(2)
        #        -DWITH_OPENCL=ON
        #        -DWITH_OPENVX=ON                  # TODO: install OpenVX and set OPENVX_ROOT
        #        -DWITH_EIGEN=ON                   # TODO: install Eigen (and probably use EIGEN_INCLUDE_PATH)
        #        -DWITH_IPP=ON
        # Cross-compilation handling
        -DCMAKE_TRY_COMPILE_TARGET_TYPE=STATIC_LIBRARY
        ${TOOLCHAIN_ARGS}
        ${ANDROID_ARGS}
)
execute_process(COMMAND ${CMAKE_COMMAND} ${OPENCV_CMAKE_ARGS} -S ${OPENCV_DOWNLOAD_DIR}/opencv-${OPENCV_VERSION} -B ${OPENCV_BUILD_DIR})
message(STATUS "Configuring OpenCV build - done")

# Start OpenCV build
message(STATUS "Building OpenCV")
execute_process(COMMAND ${CMAKE_COMMAND} --build ${OPENCV_BUILD_DIR})
message(STATUS "Building OpenCV - done")

# Modify OpenCVConfig-version.cmake to check for Android ABI mismatch
# We don't modify OpenCVConfig.cmake because that way CMake would still add OpenCV into a build even
# if Android ABI is incompatible and OpenCV is considered not found
# This implementation may produce false negative reports in stderr when OpenCV with incompatible ABI
# is considered, stating that OpenCV could not be found
if (ADD_ANDROID_ABI_CHECK)
    if (ANDROID_ABI)
        message(STATUS "Adding Android ABI check into OpenCVConfig-version.cmake")
        file(
                APPEND
                ${OPENCV_BUILD_DIR}/OpenCVConfig-version.cmake
                "
if (ANDROID_ABI AND NOT \${ANDROID_ABI} STREQUAL ${ANDROID_ABI})
    if (NOT OpenCV_FIND_QUIETLY)
        message(WARNING \"Found OpenCV but it has incompatible ABI: need \${ANDROID_ABI} but found ${ANDROID_ABI}\")
    endif ()
    set(PACKAGE_VERSION_UNSUITABLE True)
endif ()
"
        )
        message(STATUS "Adding Android ABI check into OpenCVConfig-version.cmake - done")
    else ()
        message(WARNING "Cannot add Android ABI check: ANDROID_ABI is not set")
    endif ()
endif ()
