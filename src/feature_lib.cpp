#include <opencv2/imgproc.hpp>
#include "feature_lib.h"
#include "conversions.h"

namespace featurelib {

FeatureDetector::FeatureDetector(int width, int height) : width(width), height(height) {}

void FeatureDetector::setHeight(int value) {
  height = value;
  mask = cv::Mat(height, width, CV_8U, cv::Scalar(255));
}

void FeatureDetector::setWidth(int value) {
  width = value;
  mask = cv::Mat(height, width, CV_8U, cv::Scalar(255));
}

int FeatureDetector::getWidth() const {
  return width;
}

int FeatureDetector::getHeight() const {
  return height;
}

SiftDetector::SiftDetector(int width, int height) : FeatureDetector(width, height) {}

featurelib::DetectionResult SiftDetector::detect(const std::vector<std::uint8_t> &input) const {
  auto image = vectorToMat(input, height, width);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> sift_key_points;
  cv::Mat sift_descriptors;

  sift_->detectAndCompute(image, mask, sift_key_points, sift_descriptors);

  auto key_points = convertToStructure(sift_key_points);
  auto descriptors = matTo2DVector(sift_descriptors);

  return {key_points, descriptors};
}

} // namespace featurelib
