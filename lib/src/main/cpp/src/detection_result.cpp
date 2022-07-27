#include "detection_result.h"

namespace featurelib {

DetectionResult::DetectionResult(std::vector<std::shared_ptr<StrippedKeypoint>> keypoints,
                                 std::vector<std::vector<uint8_t>> descriptors)
    : keypoints_(std::move(keypoints)), descriptors_(std::move(descriptors)) {}

std::vector<std::shared_ptr<StrippedKeypoint>> DetectionResult::getKeypoints() {
  return keypoints_;
}

std::vector<std::vector<uint8_t>> DetectionResult::getDescriptors() {
  return descriptors_;
}

}  // namespace featurelib