#include "feature_lib.h"
#include "conversions.h"
namespace libstructs {

SiftDetector::SiftDetector(int width, int height) {
  this->width = width;
  this->height = height;
}

  libstructs::CalcOutputStruct SiftDetector::calc(const std::vector<std::uint8_t> &input_vector) {
  cv::Mat Image = VectorToMat(input_vector, height, width);
  cv::cvtColor(Image, Image, cv::COLOR_BGR2GRAY);  //with that line test isn't working

  std::vector<cv::KeyPoint> SiftKeyPoints;
  cv::Mat SiftDescriptors;

  sift_instance_->detectAndCompute(Image, cv::Mat(), SiftKeyPoints, SiftDescriptors);
  return ConvertToOutputStructure(ConvertToStructure(SiftKeyPoints), MatToVector(SiftDescriptors));
}

}  // namespace libstructs


