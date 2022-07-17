
#ifndef FEATUREDETECT_FEATURELIB_H
#define FEATUREDETECT_FEATURELIB_H

#include <vector>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d.hpp>
//#include <opencv2/xfeatures2d.hpp>
//#include <opencv2/xfeatures2d/nonfree.hpp>
#include <opencv2/calib3d.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/core/utility.hpp>
#include <opencv2/core/ocl.hpp>

class featurelib  {
public:
    featurelib();
private:
    cv::Ptr<cv::Feature2D> sift = cv::SIFT::create();
};


#endif //FEATUREDETECT_FEATURELIB_H
