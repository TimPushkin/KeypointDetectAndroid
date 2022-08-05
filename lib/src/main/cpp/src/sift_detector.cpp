#include <opencv2/features2d.hpp>
#include "sift_detector.h"

namespace kpdlib {

SiftDetector::SiftDetector(int width, int height) : KeypointDetector(width, height, cv::SIFT::create()) {}

}  // namespace kpdlib
