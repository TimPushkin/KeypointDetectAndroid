#include <iostream>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include "sift_detector.h"
#include "surf_detector.h"
#include "orb_detector.h"

// Converts a matrix of bytes to a 1D vector.
std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  auto cont = mat.isContinuous() ? mat : mat.clone();  // Make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // Copy from a 1D Mat
  return result;
}

// Pretty-prints keypoints and descriptors to `stdout`.
void printOutput(const std::shared_ptr<kpdlib::DetectionResult> &output) {
  std::cout << "Keypoints" << std::endl;
  for (auto &kp : output->getKeypoints()) {
    std::cout << kp->getX() << "\t" << kp->getY() << "\t" << kp->getSize() << "\t" << kp->getAngle() << "\t"
              << kp->getStrength() << std::endl;
  }

  std::cout << "Descriptors" << std::endl;
  for (const auto &row : output->getDescriptors()) {
    for (const auto &element : row) std::cout << element << ' ';
    std::cout << std::endl;
  }
}

// Calculates keypoints and image descriptors and prints them to stdout. Image file path in format compatible with
// `imread` from OpenCV is expected as a CLI argument.
int main(int argc, char *argv[]) {
  if (argc != 2) {
    std::cerr << "Illegal CLI arguments num. Expected only a file path but was " << argc
              << " arguments (including the command name)" << std::endl;
    return 1;
  }

  // Read the image
  auto image_path = argv[1];
  auto img = cv::imread(image_path, cv::IMREAD_COLOR);

  // Convert the image into the suitable format
  auto image_data = matToVector(img);
  auto width = img.cols;
  auto height = img.rows;

  // Detect with SIFT
  kpdlib::SiftDetector sift_detector(width, height);
  auto sift_output = sift_detector.detect(image_data);
  printOutput(sift_output);

  // Detect with SURF
  kpdlib::SurfDetector surf_detector(width, height);
  auto surf_output = surf_detector.detect(image_data);
  printOutput(surf_output);

  // Detect with ORB
  kpdlib::OrbDetector orb_detector(width, height);
  auto orb_output = orb_detector.detect(image_data);
  printOutput(orb_output);

  return 0;
}
