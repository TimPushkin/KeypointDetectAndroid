#include <opencv2/imgproc.hpp>
#include "conversions.h"
#include "surf_detector.h"

namespace featurelib {

SurfDetector::SurfDetector(int width, int height) : FeatureDetector(width, height) {}

std::shared_ptr<featurelib::DetectionResult> SurfDetector::detect(const std::vector<std::uint8_t> &input) const {
  if (height_ <= 0 || width_ <= 0) {  // cv::cvtColor throws on empty input
    return std::make_shared<DetectionResult>(
            std::vector<std::shared_ptr<StrippedKeypoint>>(),
            std::vector<std::vector<uint8_t>>());
  }

  auto image = vectorToMat(input, height_, width_);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> surf_keypoints;
  cv::Mat surf_descriptors;

  surf_->detectAndCompute(image, mask_, surf_keypoints, surf_descriptors);

  auto keypoints = convertToStructure(surf_keypoints);
  auto descriptors = matTo2DVector(surf_descriptors);

  return std::make_shared<DetectionResult>(keypoints, descriptors);
}

}  // namespace featurelib
