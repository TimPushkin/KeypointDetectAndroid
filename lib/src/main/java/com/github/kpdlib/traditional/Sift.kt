package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native SIFT detector.
 */
class Sift(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(SiftDetector(width, height))
