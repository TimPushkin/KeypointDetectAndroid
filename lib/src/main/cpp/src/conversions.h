#ifndef LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_
#define LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_

#include <memory>
#include <vector>
#include <opencv2/core.hpp>
#include "stripped_keypoint.h"

// Represents an RGB pixels vector in the format of `[R_1, G_1, B_1, ..., R_n, G_n, B_n]` where `n` is at least
// `width * height` as a matrix of form `width x height` and type `CV_8UC3`. No data is copied.
cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width);

// Converts a 2D matrix of bytes to a 2D vector with the same sizes. Data is copied.
std::vector<std::vector<uint8_t>> matTo2DVector(const cv::Mat &mat);

// Converts OpenCV keypoints to stripped keypoints. Data is copied.
std::vector<std::shared_ptr<kpdlib::StrippedKeypoint>> stripKeypoints(const std::vector<cv::KeyPoint> &keypoints);

#endif  // LIB_SRC_MAIN_CPP_SRC_CONVERSIONS_H_
