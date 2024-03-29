package com.github.kpdlib

/**
 * Keypoint which represents an important image feature.
 */
interface Keypoint {
    /**
     * x coordinate of the keypoint center.
     */
    val x: Float

    /**
     * y coordinate of the keypoint center.
     */
    val y: Float

    /**
     * Keypoint strength.
     *
     * The higher the strength the "better" the keypoint.
     */
    val strength: Float

    /**
     * Diameter of the meaningful keypoint neighborhood.
     *
     * Some detectors don't provide such information, then this returns `null`.
     */
    val size: Float?

    /**
     * Orientation of the feature represented by the keypoint.
     *
     * It is in [0, 360) degrees clockwise relative to image coordinates. Some detectors don't
     * provide such information, then this returns `null`.
     */
    val angle: Float?
}
