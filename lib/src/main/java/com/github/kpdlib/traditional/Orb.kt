package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native
 * [OpenCV ORB detector](https://docs.opencv.org/4.6.0/db/d95/classcv_1_1ORB.html).
 *
 * - Keypoints have sizes and angles
 * - Descriptor size is 32
 */
class Orb(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(OrbDetector(width, height))
