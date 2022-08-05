#include <opencv2/features2d.hpp>
#include "orb_detector.h"

namespace featurelib {

OrbDetector::OrbDetector(int width, int height) : FeatureDetector(width, height, cv::ORB::create()) {}

}  // namespace featurelib
