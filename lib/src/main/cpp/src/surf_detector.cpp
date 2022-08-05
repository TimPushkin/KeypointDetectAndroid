#include <opencv2/xfeatures2d.hpp>
#include "surf_detector.h"

namespace featurelib {

SurfDetector::SurfDetector(int width, int height) : FeatureDetector(width, height, cv::xfeatures2d::SURF::create()) {}

}  // namespace featurelib
