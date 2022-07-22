#ifndef FEATUREDETECT_SRC_CONVERSIONS_H_
#define FEATUREDETECT_SRC_CONVERSIONS_H_

#include <opencv2/core.hpp>
#include "feature_lib.h"

cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width) {
  return {height, width, CV_8UC3, (std::uint8_t *) input_vector.data()};
}

std::vector<std::vector<uint8_t>> matTo2DVector(const cv::Mat &mat) {
  std::vector<std::vector<uint8_t>> result;
  auto cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous

  result.reserve(cont.rows);

  auto rowLen = cont.cols * cont.channels();
  for (auto i = 0; i < cont.rows; i++) {
    const auto *rowPtr = cont.ptr<uint8_t>(i);
    result.emplace_back(rowPtr, rowPtr + rowLen);
  }

  return result;
}

std::vector<featurelib::KeyPoint> convertToStructure(const std::vector<cv::KeyPoint> &key_points) {
  std::vector<featurelib::KeyPoint> output;
  output.reserve(key_points.size());
  for (auto &kp : key_points) {
    featurelib::KeyPoint temp = {kp.pt.x, kp.pt.y, kp.angle, kp.response, kp.size};
    output.push_back(temp);
  }
  return output;
}

#endif // FEATUREDETECT_SRC_CONVERSIONS_H_
