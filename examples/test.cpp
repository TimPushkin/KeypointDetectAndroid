#include "../include/FeatureLib.h"
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat
  return result;
}
int main() {
  std::string image_path = cv::samples::findFile("church.jpg");
  cv::Mat img = imread(image_path, cv::IMREAD_GRAYSCALE);
  std::vector<uint8_t> testVector = matToVector(img);

  int width = img.cols;
  int height = img.rows;

  FeatureLib featurelib(testVector, width, height);

  std::vector<libstructs::KeyPoint> testVectorOfKeyPoints = featurelib.getSiftKeyPoints();
  std::vector<uint8_t> testVectorOfDescriptors = featurelib.getSiftDescriptors();
  std::cout << "Printing points" << std::endl;
  for (auto &tempKp : testVectorOfKeyPoints) {
    std::cout << tempKp.x << "\t" << tempKp.y << std::endl;
  }
  std::cout << "Printing descriptors" << std::endl;

  for (auto &tempUint : testVectorOfDescriptors) {
    std::cout << tempUint << std::endl;
  }
  return 0;
}
