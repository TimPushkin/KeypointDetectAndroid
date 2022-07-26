package com.github.featuredetectandroid.utils

import android.content.Context
import com.github.featuredetectlib.learned.SuperPoint

enum class KeypointDetectionAlgorithm(val algorithmName: String) {
    NONE("None"),
    SIFT("SIFT"),
    SURF("SURF"),
    ORB("ORB"),
    SUPERPOINT("SuperPoint");

    companion object {
        val names = values().map { it.algorithmName }

        /**
         * SuperPoint, which needs a context during initialization,
         * does not retain links to it, so no context leak needs to be handled.
         */
        fun nameToFeatureDetector(
            context: Context,
            algorithmName: String,
            width: Int,
            height: Int
        ) = when (algorithmName) {
            SUPERPOINT.algorithmName -> SuperPoint(context, width, height)
            else -> null
        }
    }
}
