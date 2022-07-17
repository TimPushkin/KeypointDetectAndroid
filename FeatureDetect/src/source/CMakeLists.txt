cmake_minimum_required(VERSION 3.22.3)
#project(source)
#set(MY_SOURCE_LIB featurelib.cpp)
#add_library(source STATIC ${MY_SOURCE_LIB})
#include_directories(source)
set(SOURCE_FLS featurelib.cpp)

add_library(${PROJECT_NAME} SHARED ${SOURCE_FLS})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_SOURCE_DIR}/source)

#find_package( OpenCV REQUIRED )
#include_directories( ${OpenCV_INCLUDE_DIRS} )
#TARGET_LINK_LIBRARIES (source ${OpenCV_LIBS})