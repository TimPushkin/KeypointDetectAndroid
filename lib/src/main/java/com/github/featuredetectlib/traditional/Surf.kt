package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.FeatureDetector

/**
 * Wrapper for a native SURF detector.
 */
class Surf(width: Int, height: Int) :
    FeatureDetector by NativeDetectorWrapper(SurfDetector(width, height))
