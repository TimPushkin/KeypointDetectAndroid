#include <opencv2/xfeatures2d.hpp>
#include "surf_detector.h"

namespace kpdlib {

SurfDetector::SurfDetector(int width, int height) : KeypointDetector(width, height, cv::xfeatures2d::SURF::create()) {}

}  // namespace kpdlib
