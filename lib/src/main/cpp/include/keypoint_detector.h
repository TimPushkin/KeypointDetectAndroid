#ifndef LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_

#include <memory>
#include <vector>
#include <cstdint>
#include <limits>
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

// Detects keypoints and their descriptors on an image.
class KeypointDetector : public internal::base_object<KeypointDetector> {
 public:
  // Detects keypoints and their descriptors on an image.
  //
  // The image is provided as RGB pixels in the format of `[R_1, G_1, B_1, ..., R_n, G_n, B_n]` where `n` is at least
  // `width * height`, the order of the pixels is left to right top to bottom.
  //
  // Returns keypoints and descriptors in the corresponding order.
  std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &pixel_data) const;

  // Width of the images to be processed.
  int getWidth() const;

  void setWidth(int value);

  // Height of the images to be processed.
  int getHeight() const;

  void setHeight(int value);

 protected:
  // Creates a keypoint detector that will use the provided detector with the given initial image size.
  KeypointDetector(int width, int height, cv::Ptr<cv::Feature2D> detector);

 private:
  int width_;
  int height_;
  cv::Ptr<cv::Feature2D> detector_;
  // Determines image region to be considered during detection. It is required as an argument in OpenCV interface:
  // https://docs.opencv.org/4.6.0/d0/d13/classcv_1_1Feature2D.html#a8be0d1c20b08eb867184b8d74c15a677 -- and is always
  // set to the current full image size so that the whole image is considered.
  cv::Mat mask_ = cv::Mat(height_, width_, CV_8U, cv::Scalar(std::numeric_limits<uint8_t>::max()));
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_KEYPOINT_DETECTOR_H_
