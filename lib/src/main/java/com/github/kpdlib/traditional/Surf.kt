package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native SURF detector.
 */
class Surf(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(SurfDetector(width, height))
