#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_

#include "keypoint_detector.h"

namespace kpdlib {

class SurfDetector : public KeypointDetector {
 public:
  SurfDetector(int width, int height);
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
