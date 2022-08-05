package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native ORB detector.
 */
class Orb(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(OrbDetector(width, height))
