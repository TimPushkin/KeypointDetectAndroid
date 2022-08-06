package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native
 * [OpenCV SIFT detector](https://docs.opencv.org/4.6.0/d7/d60/classcv_1_1SIFT.html).
 *
 * - Keypoints have sizes and angles
 * - Descriptor size is 128
 */
class Sift(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(SiftDetector(width, height))
