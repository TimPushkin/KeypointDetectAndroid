#ifndef FEATUREDETECT_SRC_CONVERSIONS_H_
#define FEATUREDETECT_SRC_CONVERSIONS_H_

#include <vector>
#include <iostream>
#include <cstdint>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include "feature_lib.h"

cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width) {
  return {height, width, CV_8UC3, (std::uint8_t *) input_vector.data()};
}

std::vector<std::vector<uint8_t> > matTo2DVec(const cv::Mat &mat_in) {
  std::vector<std::vector<uint8_t> > vec_out(mat_in.rows);
  for (int i = 0; i < mat_in.rows; ++i) {
    vec_out[i].resize(mat_in.cols);
    for (int j = 0; j < mat_in.cols; ++j) {
      vec_out[i][j] = mat_in.at<uint8_t>(i, j);
    }
  }
  return vec_out;
}

std::vector<uint8_t> matTo1DVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat

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

#endif //  FEATUREDETECT_SRC_CONVERSIONS_H_
