#ifndef LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

// Detector that uses ORB from OpenCV: https://docs.opencv.org/4.6.0/db/d95/classcv_1_1ORB.html.
// - Keypoints have sizes and angles
// - Descriptor size is 32
class OrbDetector : public KeypointDetector {
 public:
  OrbDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
