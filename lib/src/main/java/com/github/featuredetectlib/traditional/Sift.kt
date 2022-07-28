package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.FeatureDetector

/**
 * Wrapper for a native SIFT detector.
 */
class Sift(width: Int, height: Int) :
    FeatureDetector by NativeDetectorWrapper(SiftDetector(width, height))
