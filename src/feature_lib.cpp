#include <opencv2/imgproc.hpp>
#include "feature_lib.h"
#include "conversions.h"

namespace featurelib {

SiftDetector::SiftDetector() = default;

SiftDetector::SiftDetector(int width, int height) {
  this->width = width;
  this->height = height;
}

featurelib::DetectionResult SiftDetector::detect(const std::vector<std::uint8_t> &input) const {
  auto image = vectorToMat(input, height, width);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);//

  std::vector<cv::KeyPoint> sift_key_points;
  cv::Mat sift_descriptors;

  sift_->detectAndCompute(image, mask, sift_key_points, sift_descriptors);

  auto key_points = convertToStructure(sift_key_points);
  auto descriptors = matTo2DVector(sift_descriptors);

  return {key_points, descriptors};
}

} // namespace featurelib
