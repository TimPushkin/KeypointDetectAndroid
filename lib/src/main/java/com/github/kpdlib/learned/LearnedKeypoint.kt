package com.github.kpdlib.learned

import com.github.kpdlib.Keypoint

/**
 * Keypoint implementation for learned approaches.
 */
internal data class LearnedKeypoint(
    override val x: Float,
    override val y: Float,
    override val strength: Float,
    override val size: Float? = null,
    override val angle: Float? = null
) : Keypoint
