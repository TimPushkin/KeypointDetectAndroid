#include "stripped_keypoint.h"

namespace featurelib {

StrippedKeypoint::StrippedKeypoint(float x, float y, float size, float angle, float strength)
    : x_(x), y_(y), size_(size), angle_(angle), strength_(strength) {}

float StrippedKeypoint::getX() const {
  return x_;
}

float StrippedKeypoint::getY() const {
  return y_;
}

float StrippedKeypoint::getSize() const {
  return size_;
}

float StrippedKeypoint::getAngle() const {
  return angle_;
}

float StrippedKeypoint::getStrength() const {
  return strength_;
}

}  // namespace featurelib
