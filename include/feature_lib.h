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
  virtual DetectionResult detect(const std::vector<std::uint8_t> &input_vector) = 0;

  void setHeight(int h) {
    height = h;
  }

  void setWidth(int w) {
    width = w;
  }

  int getWidth() const {
    return width;
  }

  int getHeight() const {
    return height;
  }

 protected:
  int width;
  int height;
};

class SiftDetector : public FeatureDetector {
 public:
  SiftDetector();

  DetectionResult detect(const std::vector<std::uint8_t> &input_vector) override;

 private:
  cv::Ptr<cv::Feature2D> sift_ = cv::SIFT::create();
};

}  // namespace featurelib

#endif // FEATUREDETECT_FEATURELIB_H_
