#ifndef LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_
#define LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_

#ifdef SCAPIX_BRIDGE

#include <scapix/bridge/object.h>

namespace featurelib {

namespace internal {

template<typename T>
using base_object = scapix::bridge::object<T>;

}  // namespace internal

}  // namespace featurelib

#else

namespace featurelib {

namespace internal {

template<typename T>
class base_object {
};

}  // namespace internal

}  // namespace featurelib

#endif  // SCAPIX_BRIDGE

namespace featurelib {

class StrippedKeypoint : public internal::base_object<StrippedKeypoint> {
 public:
  StrippedKeypoint(float x, float y, float size, float angle, float strength);

  float getX() const;

  float getY() const;

  float getSize() const;

  float getAngle() const;

  float getStrength() const;

 private:
  float x_;
  float y_;
  float size_;
  float angle_;
  float strength_;
};

}  // namespace featurelib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_
