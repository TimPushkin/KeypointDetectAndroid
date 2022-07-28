#ifndef LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_

#include <memory>
#include <vector>
#include <opencv2/features2d.hpp>
#include "feature_detector.h"

namespace featurelib {

class OrbDetector : public FeatureDetector {
 public:
  OrbDetector(int width, int height);

  std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &input) const override;

 private:
  cv::Ptr<cv::Feature2D> orb_ = cv::ORB::create();
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_ORB_DETECTOR_H_
