#include "feature_detector.h"

namespace featurelib {

FeatureDetector::FeatureDetector(int width, int height) : width_(width), height_(height) {}

int FeatureDetector::getHeight() const {
  return height_;
}

void FeatureDetector::setHeight(int value) {
  height_ = value;
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255));
}

int FeatureDetector::getWidth() const {
  return width_;
}

void FeatureDetector::setWidth(int value) {
  width_ = value;
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255));
}

}  // namespace featurelib
