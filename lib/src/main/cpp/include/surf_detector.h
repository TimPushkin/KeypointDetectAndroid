#ifndef LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_

#include <memory>
#include <vector>
#include <opencv2/xfeatures2d.hpp>
#include "feature_detector.h"

namespace featurelib {

class SurfDetector : public FeatureDetector {
 public:
  SurfDetector(int width, int height);

  std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &input) const override;

 private:
  cv::Ptr<cv::Feature2D> surf_ = cv::xfeatures2d::SURF::create();
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_SURF_DETECTOR_H_
