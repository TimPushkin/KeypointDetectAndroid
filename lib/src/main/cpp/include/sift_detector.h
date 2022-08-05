#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

class SiftDetector : public KeypointDetector {
 public:
  SiftDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
