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

class FeatureLib {
 public:
  FeatureLib(const std::vector<std::uint8_t> &input_vector, int width, int height);

  std::vector<libstructs::KeyPoint> calcSiftKeyPoints();

  std::vector<uint8_t> calcSiftDescriptors();

  std::vector<libstructs::KeyPoint> calcOrbKeyPoints();

  std::vector<uint8_t> calcOrbDescriptors();

  std::vector<libstructs::KeyPoint> calcSurfKeyPoints();

  std::vector<uint8_t> calcSurfDescriptors();

 private:
  std::vector<cv::KeyPoint> SiftKeyPoints;
  std::vector<cv::KeyPoint> OrbKeyPoints;
  std::vector<cv::KeyPoint> SurfKeyPoints;
  cv::Mat SiftDescriptors;
  cv::Mat OrbDescriptors;
  cv::Mat SurfDescriptors;
  cv::Mat Image;
  cv::Ptr<cv::Feature2D> sift_instance_ = cv::SIFT::create();
  cv::Ptr<cv::Feature2D> orb_instance_ = cv::ORB::create();

  static cv::Mat VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width);

  static std::vector<uint8_t> MatToVector(const cv::Mat &mat);

  static std::vector<libstructs::KeyPoint> ConvertToStructure(const std::vector<cv::KeyPoint> &cv_key_points_vec);

};

}  //namespace libstructs

#endif // FEATUREDETECT_FEATURELIB_H_
