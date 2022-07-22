#include "feature_lib.h"
#include "conversions.h"

namespace featurelib {

SiftDetector::SiftDetector() = default;

SiftDetector::SiftDetector(int width, int height) {
  this->width = width;
  this->height = height;
}

featurelib::DetectionResult SiftDetector::detect(const std::vector<std::uint8_t> &input) const {
  cv::Mat image = vectorToMat(input, height, width);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> key_points;
  cv::Mat descriptors;

  sift_->detectAndCompute(image, mask, key_points, descriptors);

  std::vector<KeyPoint> key_points_output = convertToStructure(key_points);
  std::vector<std::vector<uint8_t>> descriptors_output = matTo2DVector(descriptors);

  return {key_points_output, descriptors_output};
}

} // namespace featurelib
