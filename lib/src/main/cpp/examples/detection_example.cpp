#include <iostream>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include "sift_detector.h"
#include "surf_detector.h"
#include "orb_detector.h"

constexpr int CLI_ARGS_NUM = 3;
constexpr int CLI_ALGO_ARG = 1;
constexpr int CLI_PATH_ARG = 2;

// Returns a detector by its name.
std::unique_ptr<kpdlib::KeypointDetector> parseAlgoName(const std::string &name) {
  if (name == "sift") {
    std::cout << "Using SIFT." << std::endl;
    return std::make_unique<kpdlib::SiftDetector>(0, 0);
  }
  if (name == "surf") {
    std::cout << "Using SURF." << std::endl;
    return std::make_unique<kpdlib::SurfDetector>(0, 0);
  }
  if (name == "orb") {
    std::cout << "Using ORB." << std::endl;
    return std::make_unique<kpdlib::OrbDetector>(0, 0);
  }
  return nullptr;
}

// Converts a matrix of bytes to a 1D vector.
std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  auto cont = mat.isContinuous() ? mat : mat.clone();  // Make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // Copy from a 1D Mat
  return result;
}

// Pretty-prints detection result to `stdout`.
void printOutput(const std::vector<std::shared_ptr<kpdlib::StrippedKeypoint>> &keypoints,
                 const std::vector<std::vector<uint8_t>> &descriptors) {
  for (std::size_t i = 0; i < keypoints.size(); i++) {
    const auto &keypoint = keypoints[i];
    const auto &descriptor = descriptors[i];

    std::cout << "(" << keypoint->getX() << ", " << keypoint->getY() << ")" << "\t"
              << "size=" << keypoint->getSize() << "\t"
              << "angle=" << keypoint->getAngle() << "\t"
              << "strength=" << keypoint->getStrength() << "\t"
              << "(descriptor size is " << descriptor.size() << ")" << std::endl;
  }
}

// Calculates keypoints and image descriptors and prints them to stdout.
//
// Expects the following CLI arguments:
// 1. Algorithm name (sift, surf, or orb).
// 2. Path to an image file in format compatible with `imread` from OpenCV.
int main(int argc, char *argv[]) {
  if (argc != CLI_ARGS_NUM) {
    std::cerr << "Unexpected CLI arguments number: expecting algorithm name and image file path." << std::endl;
    return 1;
  }

  // Instantiate a detector
  auto detector = parseAlgoName(argv[CLI_ALGO_ARG]);
  if (detector == nullptr) {
    std::cerr << "Unknown algorithm name: supported algorithm names are sift, surf, and orb.";
    return 1;
  }

  // Obtain an image
  auto *image_path = argv[CLI_PATH_ARG];
  auto img = cv::imread(image_path, cv::IMREAD_COLOR);

  // Convert the image into the supported format
  auto image_data = matToVector(img);
  auto width = img.cols;
  auto height = img.rows;

  // Pass image size to the detector
  detector->setWidth(width);
  detector->setHeight(height);

  // Run detection
  auto output = detector->detect(image_data);

  // Use the resulting keypoints and descriptors as needed
  printOutput(output->getKeypoints(), output->getDescriptors());

  return 0;
}
