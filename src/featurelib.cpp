#include "featurelib.h"

featurelib::featurelib(const std::vector<std::uint8_t> &inputVector, int Width, int Height) {
  Image = VectorToMat(inputVector, Height, Width);

}

cv::Mat featurelib::VectorToMat(const std::vector<std::uint8_t> &inputVector, int Height, int Width) {
  return cv::Mat(Height, Width, CV_8UC1, (std::uint8_t *) inputVector.data());
}

std::vector<libstructs::KeyPoint> featurelib::ConvertToStructure(const std::vector<cv::KeyPoint> &CvKeyPointsVec) {
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

std::vector<libstructs::KeyPoint> featurelib::getSiftKeyPoints() {
  std::vector<cv::KeyPoint> SiftKeyPoints;
  sift->detect(Image, SiftKeyPoints);
  return ConvertToStructure(SiftKeyPoints);
}


