#include <utility>
#include <opencv2/imgproc.hpp>
#include "conversions.h"
#include "feature_detector.h"

namespace featurelib {

FeatureDetector::FeatureDetector(int width, int height, cv::Ptr<cv::Feature2D> detector)
    : width_(width), height_(height), detector_(std::move(detector)) {}

std::shared_ptr<DetectionResult> FeatureDetector::detect(const std::vector<std::uint8_t> &pixel_data) const {
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

int FeatureDetector::getHeight() const {
  return height_;
}

void FeatureDetector::setHeight(int value) {
  height_ = value;
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255));
}

int FeatureDetector::getWidth() const {
  return width_;
}

void FeatureDetector::setWidth(int value) {
  width_ = value;
  mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255));
}

}  // namespace featurelib
