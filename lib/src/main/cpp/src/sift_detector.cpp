#include <opencv2/features2d.hpp>
#include "sift_detector.h"

namespace featurelib {

SiftDetector::SiftDetector(int width, int height) : FeatureDetector(width, height, cv::SIFT::create()) {}

}  // namespace featurelib
