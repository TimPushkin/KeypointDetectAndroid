#include <utility>
#include <opencv2/imgproc.hpp>
#include "conversions.h"
#include "keypoint_detector.h"

namespace kpdlib {

KeypointDetector::KeypointDetector(int width, int height, cv::Ptr<cv::Feature2D> detector)
    : width_(width), height_(height), detector_(std::move(detector)) {}

std::shared_ptr<DetectionResult> KeypointDetector::detect(const std::vector<std::uint8_t> &pixel_data) const {
  if (height_ <= 0 || width_ <= 0) {  // cv::cvtColor throws on empty input
    return std::make_shared<DetectionResult>(
        std::vector<std::shared_ptr<StrippedKeypoint>>(),
        std::vector<std::vector<uint8_t>>());
  }

  auto image = vectorToMat(pixel_data, height_, width_);
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);

  std::vector<cv::KeyPoint> cv_keypoints;
  cv::Mat cv_descriptors;
  detector_->detectAndCompute(image, mask_, cv_keypoints, cv_descriptors);

  auto keypoints = stripKeypoints(cv_keypoints);
  auto descriptors = matTo2DVector(cv_descriptors);

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