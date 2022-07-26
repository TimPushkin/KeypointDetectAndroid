cmake_minimum_required(VERSION 3.18)
project(featurelib)

option(BUILD_EXAMPLES "Build examples" OFF)

add_subdirectory(src)
add_subdirectory(external)

if (BUILD_EXAMPLES)
    message(STATUS "Will build examples")
    add_subdirectory(examples)
endif ()
