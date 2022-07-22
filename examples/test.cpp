#include "../include/feature_lib.h"
#include <iostream>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>

std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat
  return result;
}
//Please inter only filepath to command line arguments
int main(int argc, char *argv[]) {
  if (argc != 2) {
    std::cerr << "Illegal CLI arguments num. Expected only a file path but was " << argc << " arguments (including the command name)"<<std::endl;
    return 1;
  }
  std::string file_path = argv[1];
  cv::Mat img = imread(file_path, cv::IMREAD_COLOR);

  int width = img.cols;
  int height = img.rows;

  std::vector<uint8_t> image_data = matToVector(img);

  featurelib::SiftDetector detector;
  detector.setWidth(width);
  detector.setHeight(height);
  featurelib::DetectionResult test_output = detector.detect(image_data);

  std::cout << "Printing points" << std::endl;
  for (auto &kp : test_output.keypoints) {
    std::cout << kp.x << "\t" << kp.y << "\t" << kp.size << "\t" << kp.angle << "\t" << kp.strength << std::endl;
  }

  std::cout << "Printing descriptors" << std::endl;
  for ( const auto &row : test_output.descriptors )
  {
    for ( const auto &s : row ) std::cout << s << ' ';
    std::cout << std::endl;
  }
  return 0;
}
