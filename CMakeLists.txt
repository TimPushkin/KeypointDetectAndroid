cmake_minimum_required(VERSION 3.18.1)
project(featureDetect)

option(BUILD_EXAMPLES "Build examples" OFF)

add_subdirectory(src)

add_subdirectory(external)

if (BUILD_EXAMPLES)
    if (NOT DEFINED SCAPIX_BRIDGE)
        message(STATUS "Will build examples")
        add_subdirectory(examples)
    else ()
        # Currently Scapix adds unwanted files to the 'examples' target
        message(WARNING "Cannot build examples when Scapix is used")
    endif ()
endif ()


