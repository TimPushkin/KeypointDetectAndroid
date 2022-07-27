set(
        SOURCE_FILES
        stripped_keypoint.cpp
        detection_result.cpp
        conversions.cpp
        feature_detector.cpp
        sift_detector.cpp
        surf_detector.cpp
        orb_detector.cpp
)

add_library(${PROJECT_NAME} SHARED ${SOURCE_FILES})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_SOURCE_DIR}/include)
