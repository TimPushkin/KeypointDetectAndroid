#include "../include/featurelib.h"
#include <vector>
#include <iostream>
#include <cstdint>
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
  std::vector<uint8_t> testVector= matToVector(img);
  int width = img.cols;
  int height = img.rows;
  std::vector<libstructs::KeyPoint> testVectorOfKeyPoints;
  featurelib featurelib(testVector, width, height);
  testVectorOfKeyPoints = featurelib.getSiftKeyPoints();
  for (auto &kp : testVectorOfKeyPoints) {
    std::cout << kp.x<<"\t"<<kp.y<<std::endl ;
  }
  return 0;
}
