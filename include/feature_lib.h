#ifndef FEATUREDETECT_FEATURELIB_H_
#define FEATUREDETECT_FEATURELIB_H_

#include <vector>
#include <iostream>
#include <cstdint>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>

namespace libstructs {

struct KeyPoint {
  float x;
  float y;
  float size;
  float angle;
  float response;
};

struct CalcOutputStruct {
  std::vector<KeyPoint> keypoints;
  std::vector<uint8_t> descriptors;
};

class FeatureDetector {
 public:
  // pure virtual function providing interface framework.
  virtual CalcOutputStruct calc(const std::vector<std::uint8_t> &input_vector) = 0;

};

class SiftDetector : public FeatureDetector {
 public:
  SiftDetector(int width, int height);

  virtual CalcOutputStruct calc(const std::vector<std::uint8_t> &input_vector);

 private:
  int width;
  int height;

  cv::Ptr<cv::Feature2D> sift_instance_ = cv::SIFT::create();
};

}  //namespace libstructs

#endif // FEATUREDETECT_FEATURELIB_H_
