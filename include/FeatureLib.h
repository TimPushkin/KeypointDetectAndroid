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

class FeatureLib {
 public:
  FeatureLib(const std::vector<std::uint8_t> &inputVector, int Width, int Height);

  std::vector<libstructs::KeyPoint> getSiftKeyPoints();

  std::vector<uint8_t> getSiftDescriptors();

  std::vector<libstructs::KeyPoint> getOrbKeyPoints();

  std::vector<uint8_t> getOrbDescriptors();

  std::vector<libstructs::KeyPoint> getSurfKeyPoints();

  std::vector<uint8_t> getSurfDescriptors();

 private:
  cv::Mat VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width);
  std::vector<uint8_t> MatToVector(const cv::Mat &mat);
  std::vector<libstructs::KeyPoint> ConvertToStructure(const std::vector<cv::KeyPoint> &CvKeyPointsVec);

  std::vector<cv::KeyPoint> SiftKeyPoints;
  std::vector<cv::KeyPoint> OrbKeyPoints;
  std::vector<cv::KeyPoint> SurfKeyPoints;
  cv::Mat SiftDescriptors;
  cv::Mat OrbDescriptors;
  cv::Mat SurfDescriptors;
  cv::Mat Image;
  cv::Ptr<cv::Feature2D> SiftInstance = cv::SIFT::create();
  cv::Ptr<cv::Feature2D> OrbInstance = cv::ORB::create();

};

#endif // FEATUREDETECT_FEATURELIB_H_
