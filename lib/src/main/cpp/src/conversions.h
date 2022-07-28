#ifndef LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_
#define LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_

#include <memory>
#include <vector>
#include <opencv2/core.hpp>
#include "stripped_keypoint.h"

cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width);

std::vector<std::vector<uint8_t>> matTo2DVector(const cv::Mat &mat);

std::vector<std::shared_ptr<featurelib::StrippedKeypoint>> convertToStructure(
    const std::vector<cv::KeyPoint> &key_points);

#endif  // LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_
