#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

// Detector that uses ORB from OpenCV: https://docs.opencv.org/4.6.0/d7/d60/classcv_1_1SIFT.html.
// - Keypoints have sizes and angles
// - Descriptor size is 128
class SiftDetector : public KeypointDetector {
 public:
  SiftDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
