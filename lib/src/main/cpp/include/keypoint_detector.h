#ifndef LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_

#include <memory>
#include <vector>
#include <cstdint>
#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include "stripped_keypoint.h"
#include "detection_result.h"

#ifdef SCAPIX_BRIDGE

#include <scapix/bridge/object.h>

namespace kpdlib {

namespace internal {

template<typename T>
using base_object = scapix::bridge::object<T>;

}  // namespace internal

}  // namespace kpdlib

#else

namespace kpdlib {

namespace internal {

template<typename T>
class base_object {
};

}  // namespace internal

}  // namespace kpdlib

#endif  // SCAPIX_BRIDGE

namespace kpdlib {

class KeypointDetector : public internal::base_object<KeypointDetector> {
 public:
  std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &pixel_data) const;

  int getHeight() const;

  void setHeight(int value);

  int getWidth() const;

  void setWidth(int value);

 protected:
  KeypointDetector(int width, int height, cv::Ptr<cv::Feature2D> detector);

 private:
  int width_;
  int height_;
  cv::Ptr<cv::Feature2D> detector_;
  cv::Mat mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(255));  // Required to pick image region for detection
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_
