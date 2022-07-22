#include "feature_lib.h"
#include "conversions.h"
#include <opencv2/imgproc.hpp>

namespace featurelib {

SiftDetector::SiftDetector() = default;

SiftDetector::SiftDetector(int width, int height) {
  this->width = width;
  this->height = height;
}

featurelib::DetectionResult SiftDetector::detect(const std::vector<std::uint8_t> &input) const {
  cv::Mat image = vectorToMat(input, height, width);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> sift_key_points;
  cv::Mat sift_descriptors;

  sift_->detectAndCompute(image, mask, sift_key_points, sift_descriptors);

  std::vector<KeyPoint> key_points = convertToStructure(sift_key_points);
  std::vector<std::vector<uint8_t>> descriptors = matTo2DVector(sift_descriptors);

  return {key_points, descriptors};
}

} // namespace featurelib
