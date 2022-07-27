#include <opencv2/imgproc.hpp>
#include "conversions.h"
#include "orb_detector.h"

namespace featurelib {

OrbDetector::OrbDetector(int width, int height) : FeatureDetector(width, height) {}

std::shared_ptr<featurelib::DetectionResult>
OrbDetector::detect(const std::vector<std::uint8_t> &input) const {
  if (height_ <= 0 || width_ <= 0) {  // cv::cvtColor throws on empty input
    return std::make_shared<DetectionResult>(
        std::vector<std::shared_ptr<StrippedKeypoint>>(),
        std::vector<std::vector<uint8_t>>());
  }

  auto image = vectorToMat(input, height_, width_);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> orb_keypoints;
  cv::Mat orb_descriptors;

  orb_->detectAndCompute(image, mask_, orb_keypoints, orb_descriptors);

  auto keypoints = convertToStructure(orb_keypoints);
  auto descriptors = matTo2DVector(orb_descriptors);

  return std::make_shared<DetectionResult>(keypoints, descriptors);
}

}  // namespace featurelib