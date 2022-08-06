package com.github.kpdlib.traditional

import com.github.kpdlib.KeypointDetector

/**
 * Wrapper for a native
 * [OpenCV SURF detector](https://docs.opencv.org/4.6.0/d5/df7/classcv_1_1xfeatures2d_1_1SURF.html).
 *
 * - Keypoints have sizes and angles
 * - Descriptor size is 64
 */
class Surf(width: Int, height: Int) :
    KeypointDetector by NativeDetectorWrapper(SurfDetector(width, height))
