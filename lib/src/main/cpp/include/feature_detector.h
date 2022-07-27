#ifndef FEATURELIB_INCLUDE_FEATURELIB_H_
#define FEATURELIB_INCLUDE_FEATURELIB_H_

#include <vector>
#include <cstdint>
#include <opencv2/core.hpp>
#include "stripped_keypoint.h"
#include "detection_result.h"

#ifdef SCAPIX_BRIDGE

#include <scapix/bridge/object.h>

namespace featurelib {

namespace internal {

template<typename T>
using base_object = scapix::bridge::object<T>;

} // namespace internal

} // namespace featurelib

#else

namespace featurelib {

namespace internal {

template<typename T>
class base_object {
};

} // namespace internal

} // namespace featurelib

#endif  // SCAPIX_BRIDGE

namespace featurelib {

class FeatureDetector : public internal::base_object<FeatureDetector> {
 public:
  FeatureDetector(int width, int height);

  virtual std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &input_vector) const = 0;

  int getHeight() const;

  void setHeight(int value);

  int getWidth() const;

  void setWidth(int value);

 protected:
  int width_;
  int height_;
  cv::Mat mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255)); // Required to pick image region for detection
};

} // namespace featurelib

#endif // FEATURELIB_INCLUDE_FEATURELIB_H_
