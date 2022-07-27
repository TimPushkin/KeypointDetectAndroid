package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.FeatureDetector

/**
 * Wrapper for a native ORB detector.
 */
class Orb(width: Int, height: Int) :
    FeatureDetector by NativeDetectorWrapper(OrbDetector(width, height))
