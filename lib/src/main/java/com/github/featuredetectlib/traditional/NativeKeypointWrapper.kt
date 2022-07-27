package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.Keypoint

/**
 * Wrapper for a native keypoint.
 */
internal data class NativeKeypointWrapper(private val keypoint: KeyPoint) : Keypoint {
    override val x: Float
        get() = keypoint.x
    override val y: Float
        get() = keypoint.y
    override val strength: Float
        get() = keypoint.strength
    override val size: Float
        get() = keypoint.size
    override val angle: Float?
        get() = keypoint.angle.takeIf { it != -1f } // angle == -1 if not applicable
}
