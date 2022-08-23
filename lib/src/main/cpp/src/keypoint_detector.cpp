#include <utility>
#include <opencv2/imgproc.hpp>
#include "logging.h"
#include "conversions.h"
#include "keypoint_detector.h"

namespace kpdlib {

constexpr auto kTag = "KeypointDetector";

KeypointDetector::KeypointDetector(int width, int height, cv::Ptr<cv::Feature2D> detector)
    : width_(width), height_(height), detector_(std::move(detector)) {}

std::shared_ptr<DetectionResult> KeypointDetector::detect(const std::vector<std::uint8_t> &pixel_data) const {
  if (width_ <= 0 || height_ <= 0) {  // cv::cvtColor throws on empty input
    logW(kTag, "Current size is negative (%d x %d), no detection is possible.", width_, height_);
    return std::make_shared<DetectionResult>(
        std::vector<std::shared_ptr<StrippedKeypoint>>(),
        std::vector<std::vector<uint8_t>>());
  }

  logV(kTag, "Starting detection (current size is %d x %d).", width_, height_);

  auto image = vectorToMat(pixel_data, height_, width_);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> cv_keypoints;
  cv::Mat cv_descriptors;
  detector_->detectAndCompute(image, mask_, cv_keypoints, cv_descriptors);

  auto keypoints = stripKeypoints(cv_keypoints);
  auto descriptors = matTo2DVector(cv_descriptors);

  logV(kTag, "Detected %lu keypoints (and %lu descriptors).", keypoints.size(), descriptors.size());

  return std::make_shared<DetectionResult>(keypoints, descriptors);
}

int KeypointDetector::getHeight() const {
  return height_;
}

void KeypointDetector::setHeight(int value) {
  height_ = value;
  // Adjust mask for the new size
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(std::numeric_limits<uint8_t>::max()));
}

int KeypointDetector::getWidth() const {
  return width_;
}

void KeypointDetector::setWidth(int value) {
  width_ = value;
  // Adjust mask for the new size
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(std::numeric_limits<uint8_t>::max()));
}

}  // namespace kpdlib
