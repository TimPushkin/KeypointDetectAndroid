#ifndef FEATUREDETECT_FEATURELIB_H_
#define FEATUREDETECT_FEATURELIB_H_

#include <vector>
#include <opencv2/features2d.hpp>

namespace featurelib {

struct KeyPoint {
  float x;
  float y;
  float size;
  float angle;
  float strength;
};

struct DetectionResult {
  std::vector<KeyPoint> keypoints;
  std::vector<std::vector<uint8_t>> descriptors;
};

class FeatureDetector {
 public:
  FeatureDetector(int width, int height);

  virtual DetectionResult detect(const std::vector<std::uint8_t> &input_vector) const = 0;

  void setHeight(int value);

  void setWidth(int value);

  int getWidth() const;

  int getHeight() const;

 protected:
  int width = 0;
  int height = 0;
  // You can limit your search to a set of regions with a mask.
  // It must be a 8-bit integer matrix with non-zero values in the region of interest.
  cv::Mat mask = cv::Mat(255 * cv::Mat::ones(height, width, CV_8U));
};

class SiftDetector : public FeatureDetector {
 public:
  SiftDetector(int width, int height);

  DetectionResult detect(const std::vector<std::uint8_t> &input) const override;

 private:
  cv::Ptr<cv::Feature2D> sift_ = cv::SIFT::create();
};

} // namespace featurelib

#endif // FEATUREDETECT_FEATURELIB_H_
