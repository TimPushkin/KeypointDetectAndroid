#include "../include/feature_lib.h"
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>

std::vector<uint8_t> matToVector(const cv::Mat &mat) {
  std::vector<uint8_t> result;
  cv::Mat cont = mat.isContinuous() ? mat : mat.clone();  // make sure the Mat is continuous
  cont.reshape(1, 1).copyTo(result);  // copy from a 1D Mat
  return result;
}

int main(int argc, char *argv[]) {

  if (argc != 2) {
    std::cerr << "Illegal CLI arguments num. Expected only filepath (including the command name) but was "
              << argc;
    return 1;
  }
  std::string file_path = argv[1];
  std::string image_path = cv::samples::findFile(file_path);
  cv::Mat img = imread(file_path, cv::IMREAD_COLOR);
  std::vector<uint8_t> test_vector = matToVector(img);

  int width = img.cols;
  int height = img.rows;

  libstructs::FeatureLib featurelib(test_vector, width, height);

  std::vector<libstructs::KeyPoint> test_vector_of_key_points = featurelib.calcSiftKeyPoints();
  std::vector<uint8_t> test_vector_of_descriptors = featurelib.calcSiftDescriptors();

  std::cout << "Printing points" << std::endl;
  for (auto &temp_kp : test_vector_of_key_points) {
    std::cout << temp_kp.x << "\t" << temp_kp.y << std::endl;
  }

  std::cout << "Printing descriptors" << std::endl;
  for (auto &temp_uint : test_vector_of_descriptors) {
    std::cout << temp_uint << std::endl;
  }

  return 0;
}
