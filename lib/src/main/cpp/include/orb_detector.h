#ifndef LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_

#include "feature_detector.h"

namespace featurelib {

class OrbDetector : public FeatureDetector {
 public:
  OrbDetector(int width, int height);
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
