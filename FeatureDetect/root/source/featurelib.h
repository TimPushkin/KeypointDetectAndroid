#ifndef FEATUREDETECT_FEATURELIB_H_
#define FEATUREDETECT_FEATURELIB_H_

#include <vector>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>

class featurelib  {
public:
     featurelib();
private:
     cv::Ptr<cv::Feature2D> sift = cv::SIFT::create();
};

#endif //FEATUREDETECT_FEATURELIB_H_
