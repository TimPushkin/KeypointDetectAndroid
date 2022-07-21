#include <vector>
#include <iostream>
#include <cstdint>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include "feature_lib.h"

#ifndef FEATUREDETECT_SRC_CONVERSIONS_H_
#define FEATUREDETECT_SRC_CONVERSIONS_H_
cv::Mat VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width) {
  return {Height, Width, CV_8UC3, (std::uint8_t *) inputVector.data()};
}

std::vector<uint8_t> MatToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat

  return result;
}

std::vector<libstructs::KeyPoint> ConvertToStructure(const std::vector<cv::KeyPoint> &cv_key_points_vec) {
  std::vector<libstructs::KeyPoint> output;
  for (auto &kp : cv_key_points_vec) {
    libstructs::KeyPoint temp = {kp.pt.x, kp.pt.y, kp.angle, kp.response, kp.size};
    output.reserve(5);
    output.push_back(temp);
  }
  return output;
}

libstructs::CalcOutputStruct ConvertToOutputStructure(const std::vector<libstructs::KeyPoint> cv_key_points_vec,
                                                      const std::vector<uint8_t> descriptors) {
  libstructs::CalcOutputStruct output;
  output.keypoints = cv_key_points_vec;
  output.descriptors = descriptors;
  return output;
}
#endif //FEATUREDETECT_SRC_CONVERSIONS_H_
