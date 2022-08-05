#include <iostream>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include "sift_detector.h"
#include "surf_detector.h"
#include "orb_detector.h"

std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  auto cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat
  return result;
}

void printOutput(const std::shared_ptr<kpdlib::DetectionResult> &output) {
  std::cout << "Printing points" << std::endl;
  for (auto &kp : output->getKeypoints()) {
    std::cout << kp->getX() << "\t" << kp->getY() << "\t" << kp->getSize() << "\t" << kp->getAngle() << "\t"
              << kp->getStrength() << std::endl;
  }

  std::cout << "Printing descriptors" << std::endl;
  for (const auto &row : output->getDescriptors()) {
    for (const auto &element : row) std::cout << element << ' ';
    std::cout << std::endl;
  }
}

// Calculates key points and image descriptors
// Takes an image file path as input
// Prints keypoints and image descriptors to stdout
int main(int argc, char *argv[]) {
  if (argc != 2) {
    std::cerr << "Illegal CLI arguments num. Expected only a file path but was " << argc
              << " arguments (including the command name)" << std::endl;
    return 1;
  }
  auto file_path = argv[1];

  auto img = imread(file_path, cv::IMREAD_COLOR);
  auto width = img.cols;
  auto height = img.rows;

  auto image_data = matToVector(img);

  kpdlib::SiftDetector sift_detector(width, height);
  auto sift_output = sift_detector.detect(image_data);

  kpdlib::OrbDetector orb_detector(width, height);
  auto orb_output = orb_detector.detect(image_data);

  kpdlib::SurfDetector surf_detector(width, height);
  auto surf_output = surf_detector.detect(image_data);

  printOutput(sift_output);
  printOutput(orb_output);
  printOutput(surf_output);

  return 0;
}
