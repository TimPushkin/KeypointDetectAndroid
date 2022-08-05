#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_

#include "feature_detector.h"

namespace featurelib {

class SiftDetector : public FeatureDetector {
 public:
  SiftDetector(int width, int height);
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SIFT_DETECTOR_H_
