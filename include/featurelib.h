#ifndef FEATUREDETECT_FEATURELIB_H_
#define FEATUREDETECT_FEATURELIB_H_

#include <vector>
#include <iostream>
#include <cstdint>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>
namespace libstructs {
struct KeyPoint {
  float x;
  float y;
  float size;
  float angle;
  float response;
};
}

class featurelib {
 public:
  featurelib(const std::vector<std::uint8_t> &inputVector, int Width, int Height);

  std::vector<libstructs::KeyPoint> getSiftKeyPoints();

 private:
  cv::Mat VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width);

  std::vector<libstructs::KeyPoint> ConvertToStructure(const std::vector<cv::KeyPoint> &CvKeyPointsVec);

  cv::Mat Image;
  cv::Ptr<cv::Feature2D> sift = cv::SIFT::create();
};

#endif // FEATUREDETECT_FEATURELIB_H_
