cmake_minimum_required(VERSION 3.20)
project(kpdlib)

option(BUILD_EXAMPLES "Build examples" OFF)

add_subdirectory(src)

if (BUILD_EXAMPLES)
    message(STATUS "Will build examples")
    add_subdirectory(examples)
endif ()

add_subdirectory(external)
