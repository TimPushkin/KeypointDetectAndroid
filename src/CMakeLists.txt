cmake_minimum_required(VERSION 3.18.1)

set(SOURCE_FILES featurelib.cpp)

add_library(${PROJECT_NAME} SHARED ${SOURCE_FILES})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_SOURCE_DIR}/include)
