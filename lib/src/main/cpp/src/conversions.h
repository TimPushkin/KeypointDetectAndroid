#ifndef FEATUREDETECT_SRC_CONVERSIONS_H_
#define FEATUREDETECT_SRC_CONVERSIONS_H_

#include <opencv2/core.hpp>
#include "stripped_keypoint.h"

cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width);

std::vector<std::vector<uint8_t>> matTo2DVector(const cv::Mat &mat);

std::vector<std::shared_ptr<featurelib::StrippedKeypoint>> convertToStructure(const std::vector<cv::KeyPoint> &key_points);

#endif // FEATUREDETECT_SRC_CONVERSIONS_H_
