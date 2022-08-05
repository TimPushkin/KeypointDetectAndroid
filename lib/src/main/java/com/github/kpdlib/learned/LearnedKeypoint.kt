package com.github.kpdlib.learned

import com.github.kpdlib.Keypoint

/**
 * Keypoint which represents an image feature.
 */
internal data class LearnedKeypoint(
    override val x: Float,
    override val y: Float,
    override val strength: Float,
    override val size: Float? = null,
    override val angle: Float? = null
) : Keypoint
