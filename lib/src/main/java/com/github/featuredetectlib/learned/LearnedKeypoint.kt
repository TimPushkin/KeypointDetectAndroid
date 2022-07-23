package com.github.featuredetectlib.learned

import com.github.featuredetectlib.Keypoint

/**
 * Keypoint which represents an image feature.
 */
data class LearnedKeypoint(
    override val x: Float,
    override val y: Float,
    override val strength: Float,
    override val size: Float? = null,
    override val angle: Float? = null
) : Keypoint
