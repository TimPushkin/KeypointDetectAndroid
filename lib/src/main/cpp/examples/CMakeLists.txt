add_executable(detection_example detection_example.cpp)

target_link_libraries(detection_example ${PROJECT_NAME})  # opencv_imgcodecs is also linked later
