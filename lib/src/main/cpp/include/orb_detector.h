#ifndef LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

class OrbDetector : public KeypointDetector {
 public:
  OrbDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
