#include <iostream>
#include <vector>
#include <map>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include "feature_lib.h"

template<typename Iterator>
void advance_all(Iterator &iterator) {
  ++iterator;
}
template<typename Iterator, typename ... Iterators>
void advance_all(Iterator &iterator, Iterators &... iterators) {
  ++iterator;
  advance_all(iterators...);
}
template<typename Function, typename Iterator, typename ... Iterators>
Function zip(Function func, Iterator begin,
             Iterator end,
             Iterators ... iterators) {
  for (; begin != end; ++begin, advance_all(iterators...))
    func(*begin, *(iterators)...);
  //could also make this a tuple
  return func;
}

std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat
  return result;
}

void printOutput(const featurelib::DetectionResult &output, std::string &algorithm) {
  std::cout << algorithm << " keypoints and size of associated descriptors arrays" << std::endl;
  zip(
      [](featurelib::KeyPoint kp, std::vector<uint8_t> row) {

        std::cout << kp.x << "\t" << kp.y << "\t" << kp.size << "\t" << kp.angle << "\t" << kp.strength << "\t"
                  << row.size() << std::endl;
      },
      output.keypoints.begin(), output.keypoints.end(), output.descriptors.begin());
}

// Calculates key points and image descriptors
// Takes an image file path as input
// Prints key points and image descriptors to stdout
int main(int argc, char *argv[]) {
  std::map<std::string, int> hash{{"wrong", 0}, {"sift", 1}, {"surf", 2}, {"orb", 3}};

  if (argc != 3) {
    std::cerr << "Illegal CLI arguments num. Expected file path and detector name but was " << argc
              << " arguments (including the command name)" << std::endl;
    return 1;
  }
  auto file_path = argv[1];
  std::string algorithm = argv[2];
  std::transform(algorithm.begin(), algorithm.end(), algorithm.begin(), ::tolower);
  try {
    if (!hash.count(algorithm))
      throw "No algo matching try again with std::cin:";
  }
  catch (const char *exception) {
    std::cerr << "Error: " << exception << '\n';
    std::cin >> algorithm;
  }
  cv::Mat img = imread(file_path, cv::IMREAD_COLOR);
  auto width = img.cols;
  auto height = img.rows;
  featurelib::DetectionResult output;
  std::vector<uint8_t> image_data = matToVector(img);
  switch (hash[algorithm]) {
    case 1: {
      featurelib::SiftDetector sift_detector(width, height);
      output = sift_detector.detect(image_data);
      break;
    }
    case 2: {
      featurelib::SurfDetector surf_detector(width, height);
      output = surf_detector.detect(image_data);
      break;
    }
    case 3: {
      featurelib::OrbDetector orb_detector(width, height);
      output = orb_detector.detect(image_data);
      break;
    }
    default: {
      std::cout << "wrong algorithm name" << std::endl;
      break;
    }
  }
  printOutput(output, algorithm);

  return 0;
}
