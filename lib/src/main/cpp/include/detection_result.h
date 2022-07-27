#ifndef LIB_SRC_MAIN_CPP_INCLUDE_DETECTION_RESULT_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_DETECTION_RESULT_H_

#include <memory>
#include <vector>
#include <cstdint>
#include "stripped_keypoint.h"

#ifdef SCAPIX_BRIDGE

#include <scapix/bridge/object.h>

namespace featurelib {

namespace internal {

template<typename T>
using base_object = scapix::bridge::object<T>;

}  // namespace internal

}  // namespace featurelib

#else

namespace featurelib {

namespace internal {

template<typename T>
class base_object {
};

}  // namespace internal

}  // namespace featurelib

#endif  // SCAPIX_BRIDGE

namespace featurelib {

class DetectionResult : public internal::base_object<DetectionResult> {
 public:
  DetectionResult(std::vector<std::shared_ptr<StrippedKeypoint>> keypoints,
                  std::vector<std::vector<uint8_t>> descriptors);

  std::vector<std::shared_ptr<StrippedKeypoint>> getKeypoints();

  std::vector<std::vector<uint8_t>> getDescriptors();

 private:
  std::vector<std::shared_ptr<StrippedKeypoint>> keypoints_;
  std::vector<std::vector<uint8_t>> descriptors_;
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_DETECTION_RESULT_H_
