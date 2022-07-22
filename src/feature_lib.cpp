#include "feature_lib.h"
#include "conversions.h"

namespace featurelib {

SiftDetector::SiftDetector() {}

featurelib::DetectionResult SiftDetector::detect(const std::vector<std::uint8_t> &input_vector) {
  cv::Mat image = vectorToMat(input_vector, height, width);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> sift_key_points;
  cv::Mat sift_descriptors;
  cv::Mat mask(255 * cv::Mat::ones(height, width,CV_8U));
  sift_->detectAndCompute(image, mask, sift_key_points, sift_descriptors);
  std::vector<KeyPoint> key_points_output = convertToStructure(sift_key_points);
  std::vector<std::vector<uint8_t>> descriptors_output = matTo2DVec(sift_descriptors);
  return {key_points_output, descriptors_output};
}

}  // namespace featurelib
