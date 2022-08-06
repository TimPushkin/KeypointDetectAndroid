#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

// Detector that uses ORB from OpenCV: https://docs.opencv.org/4.6.0/db/d95/classcv_1_1ORB.html.
// - Keypoints have sizes and angles
// - Descriptor size is 64
class SurfDetector : public KeypointDetector {
 public:
  SurfDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
