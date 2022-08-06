package com.github.kpdlib.traditional

import com.github.kpdlib.Keypoint

private const val ANGLE_NA = -1f // OpenCV sets angle to -1 if not applicable

/**
 * Wrapper for a stripped native
 * [OpenCV keypoint](https://docs.opencv.org/4.6.0/d2/d29/classcv_1_1KeyPoint.html).
 */
internal data class NativeKeypointWrapper(private val keypoint: StrippedKeypoint) : Keypoint {
    override val x: Float
        get() = keypoint.x
    override val y: Float
        get() = keypoint.y
    override val strength: Float
        get() = keypoint.strength
    override val size: Float
        get() = keypoint.size
    override val angle: Float?
        get() = keypoint.angle.takeIf { it != ANGLE_NA }
}
