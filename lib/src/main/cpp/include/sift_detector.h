#ifndef FEATURELIB_INCLUDE_SIFT_DETECTOR_H_
#define FEATURELIB_INCLUDE_SIFT_DETECTOR_H_

#include <opencv2/features2d.hpp>
#include "feature_detector.h"

namespace featurelib {

class SiftDetector : public FeatureDetector {
 public:
  SiftDetector(int width, int height);

  std::shared_ptr<DetectionResult> detect(const std::vector<std::uint8_t> &input) const override;

 private:
  cv::Ptr<cv::Feature2D> sift_ = cv::SIFT::create();
};

} // namespace featurelib

#endif // FEATURELIB_INCLUDE_SIFT_DETECTOR_H_
