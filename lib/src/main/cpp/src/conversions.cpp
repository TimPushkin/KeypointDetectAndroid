#include "conversions.h"

cv::Mat vectorToMat(const std::vector<std::uint8_t> &input_vector, int height, int width) {
  return {height, width, CV_8UC3, const_cast<std::uint8_t *>(input_vector.data())};
}

std::vector<std::vector<uint8_t>> matTo2DVector(const cv::Mat &mat) {
  std::vector<std::vector<uint8_t>> result;
  auto cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous

  result.reserve(cont.rows);

  auto rowLen = cont.cols * cont.channels();
  for (auto i = 0; i < cont.rows; i++) {
    const auto *rowPtr = cont.ptr<uint8_t>(i);
    result.emplace_back(rowPtr, rowPtr + rowLen);
  }

  return result;
}

std::vector<std::shared_ptr<featurelib::StrippedKeypoint>> convertToStructure(const std::vector<cv::KeyPoint> &key_points) {
  std::vector<std::shared_ptr<featurelib::StrippedKeypoint>> output;
  output.reserve(key_points.size());
  for (auto &kp : key_points) {
    output.emplace_back(std::make_shared<featurelib::StrippedKeypoint>(kp.pt.x, kp.pt.y, kp.angle, kp.response, kp.size));
  }
  return output;
}
