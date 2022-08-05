#include <opencv2/features2d.hpp>
#include "orb_detector.h"

namespace kpdlib {

OrbDetector::OrbDetector(int width, int height) : KeypointDetector(width, height, cv::ORB::create()) {}

}  // namespace kpdlib
