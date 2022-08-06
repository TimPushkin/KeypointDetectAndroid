#ifndef LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_

#ifdef SCAPIX_BRIDGE

#include <scapix/bridge/object.h>

namespace kpdlib {

namespace internal {

template<typename T>
using base_object = scapix::bridge::object<T>;

}  // namespace internal

}  // namespace kpdlib

#else

namespace kpdlib {

namespace internal {

template<typename T>
class base_object {
};

}  // namespace internal

}  // namespace kpdlib

#endif  // SCAPIX_BRIDGE

namespace kpdlib {

// Keypoint which represents an important image feature. It is a stripped version of OpenCV keypoint:
// https://docs.opencv.org/4.6.0/d2/d29/classcv_1_1KeyPoint.html.
class StrippedKeypoint : public internal::base_object<StrippedKeypoint> {
 public:
  StrippedKeypoint(float x, float y, float size, float angle, float strength);

  // x coordinate of the keypoint center.
  float getX() const;

  // y coordinate of the keypoint center.
  float getY() const;

  // Diameter of the meaningful keypoint neighborhood.
  float getSize() const;

  // Orientation of the feature represented by the keypoint. It is in [0, 360) degrees clockwise relative to image
  // coordinates. Some detectors don't provide such information, then this returns `-1`.
  float getAngle() const;

  // Keypoint strength. The higher the strength the "better" the keypoint.
  float getStrength() const;

 private:
  float x_;
  float y_;
  float size_;
  float angle_;
  float strength_;
};

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_
