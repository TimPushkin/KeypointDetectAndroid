package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.Keypoint

private const val ANGLE_NA = -1f // OpenCV sets angle to -1 if it is not applicable

/**
 * Wrapper for a native keypoint.
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
