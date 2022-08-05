#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_

#include "feature_detector.h"

namespace featurelib {

class SurfDetector : public FeatureDetector {
 public:
  SurfDetector(int width, int height);
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
