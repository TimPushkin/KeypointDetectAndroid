message(STATUS "Configuring Scapix for ${SCAPIX_BRIDGE}")

set(
        HEADER_FILES
        ${CMAKE_SOURCE_DIR}/include/stripped_keypoint.h
        ${CMAKE_SOURCE_DIR}/include/detection_result.h
        ${CMAKE_SOURCE_DIR}/include/feature_detector.h
        ${CMAKE_SOURCE_DIR}/include/sift_detector.h
        ${CMAKE_SOURCE_DIR}/include/surf_detector.h
        ${CMAKE_SOURCE_DIR}/include/orb_detector.h
)

include(FetchContent)
FetchContent_Declare(
        cmodule
        URL "https://github.com/scapix-com/cmodule/archive/refs/tags/v1.0.35.tar.gz"
        URL_HASH SHA256=3eeabad538e9e06bc21f4ccafccfa97808b8458044ee32f28a37b81e7ce8d174
)
FetchContent_MakeAvailable(cmodule)

find_package(Scapix REQUIRED)

scapix_bridge_headers(${PROJECT_NAME} com.github.featuredetectlib.traditional ${HEADER_FILES})

message(STATUS "Configuring Scapix - done")
