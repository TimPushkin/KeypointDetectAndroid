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

}  // namespace kpdlib

#endif  // LIB_SRC_MAIN_CPP_INCLUDE_STRIPPED_KEYPOINT_H_
