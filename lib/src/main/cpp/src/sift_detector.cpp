#include <opencv2/imgproc.hpp>
#include "conversions.h"
#include "sift_detector.h"

namespace featurelib {

SiftDetector::SiftDetector(int width, int height) : FeatureDetector(width, height) {}

std::shared_ptr<featurelib::DetectionResult> SiftDetector::detect(const std::vector<std::uint8_t> &input) const {
  if (height_ <= 0 || width_ <= 0) {  // cv::cvtColor throws on empty input
    return std::make_shared<DetectionResult>(
            std::vector<std::shared_ptr<StrippedKeypoint>>(),
            std::vector<std::vector<uint8_t>>());
  }

  auto image = vectorToMat(input, height_, width_);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> sift_keypoints;
  cv::Mat sift_descriptors;

  sift_->detectAndCompute(image, mask_, sift_keypoints, sift_descriptors);

  auto keypoints = convertToStructure(sift_keypoints);
  auto descriptors = matTo2DVector(sift_descriptors);

  return std::make_shared<DetectionResult>(keypoints, descriptors);
}

}  // namespace featurelib
