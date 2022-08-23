#include "logging.h"

#ifndef NDEBUG

#if __has_include(<android/log.h>)

#include <android/log.h>
#include <cstdarg>

void logV(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  __android_log_vprint(ANDROID_LOG_VERBOSE, tag, format, args);
  va_end(args);
}

void logD(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  __android_log_vprint(ANDROID_LOG_DEBUG, tag, format, args);
  va_end(args);
}

void logI(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  __android_log_vprint(ANDROID_LOG_DEBUG, tag, format, args);
  va_end(args);
}

void logW(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  __android_log_vprint(ANDROID_LOG_DEBUG, tag, format, args);
  va_end(args);
}

void logE(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  __android_log_vprint(ANDROID_LOG_DEBUG, tag, format, args);
  va_end(args);
}

#else  // __has_include(<android/log.h>)

#include <cstdio>
#include <cstdarg>

static void log(const char *lvl, const char *tag, const char *fmt, std::va_list args) {
  std::fprintf(stderr, "%s %s: ", lvl, tag);
  std::vfprintf(stderr, fmt, args);
  std::fprintf(stderr, "\n");
}

void logV(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  log("V", tag, format, args);
  va_end(args);
}

void logD(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  log("D", tag, format, args);
  va_end(args);
}

void logI(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  log("I", tag, format, args);
  va_end(args);
}

void logW(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  log("W", tag, format, args);
  va_end(args);
}

void logE(const char *tag, const char *format...) {
  std::va_list args;
  va_start(args, format);
  log("E", tag, format, args);
  va_end(args);
}

#endif  // __has_include(<android/log.h>)

#else  // NDEBUG

void logV(const char *tag, const char *format...) {}

void logD(const char *tag, const char *format...) {}

void logI(const char *tag, const char *format...) {}

void logW(const char *tag, const char *format...) {}

void logE(const char *tag, const char *format...) {}

#endif  // NDEBUG
