#include "FeatureLib.h"

FeatureLib::FeatureLib(const std::vector<std::uint8_t> &inputVector, int Width, int Height) {
  Image = VectorToMat(inputVector, Height, Width);
}

cv::Mat FeatureLib::VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width) {
  return cv::Mat(Height, Width, CV_8UC1, (std::uint8_t *) inputVector.data());
}

std::vector<uint8_t> FeatureLib::MatToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  if (mat.isContinuous()) {
    result.assign((uint8_t *) mat.datastart, (uint8_t *) mat.dataend);
  } else {
    for (int i = 0; i < mat.rows; ++i) {
      result.insert(result.end(), (uint8_t *) mat.ptr<uchar>(i), (uint8_t *) mat.ptr<uchar>(i) + mat.cols);
    }
  }
  return result;
}

std::vector<libstructs::KeyPoint> FeatureLib::ConvertToStructure(const std::vector<cv::KeyPoint> &CvKeyPointsVec) {
  std::vector<libstructs::KeyPoint> output;
  for (auto &kp : CvKeyPointsVec) {
    libstructs::KeyPoint temp;
    temp.x = kp.pt.x;
    temp.y = kp.pt.y;
    temp.angle = kp.angle;
    temp.response = kp.response;
    temp.size = kp.size;
    output.push_back(temp);
  }
  return output;
}

std::vector<libstructs::KeyPoint> FeatureLib::getSiftKeyPoints() {
  SiftInstance->detect(Image, SiftKeyPoints);
  return ConvertToStructure(SiftKeyPoints);
}
std::vector<uint8_t> FeatureLib::getSiftDescriptors() {
  SiftInstance->compute(Image, SiftKeyPoints, SiftDescriptors);
  return MatToVector(SiftDescriptors);
}




