#include "feature_lib.h"

namespace libstructs {

cv::Mat FeatureLib::VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width) {
  return {Height, Width, CV_8UC3, (std::uint8_t *) inputVector.data()};
}

std::vector<uint8_t> FeatureLib::MatToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat

  return result;
}

std::vector<libstructs::KeyPoint> FeatureLib::ConvertToStructure(const std::vector<cv::KeyPoint> &cv_key_points_vec) {
  std::vector<libstructs::KeyPoint> output;
  for (auto &kp : cv_key_points_vec) {
    libstructs::KeyPoint temp = {kp.pt.x, kp.pt.y, kp.angle, kp.response, kp.size};
    output.reserve(5);
    output.push_back(temp);
  }
  return output;
}

FeatureLib::FeatureLib(const std::vector<std::uint8_t> &input_vector, int width, int height) {
  Image = VectorToMat(input_vector, height, width);
  cv::cvtColor(Image, Image, cv::COLOR_BGR2GRAY);  //with that line test isn't working
}

std::vector<libstructs::KeyPoint> FeatureLib::calcSiftKeyPoints() {
  sift_instance_->detect(Image, SiftKeyPoints);
  return ConvertToStructure(SiftKeyPoints);
}
std::vector<uint8_t> FeatureLib::calcSiftDescriptors() {
  sift_instance_->compute(Image, SiftKeyPoints, SiftDescriptors);
  return MatToVector(SiftDescriptors);
}

}  // namespace libstructs


