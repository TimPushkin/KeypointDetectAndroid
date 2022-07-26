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

        /** SuperPoint, which needs a context during initialization,
         * does not contain it inside itself, and therefore does not require any processing
         * in an activity's onDestroy and onResume. */
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
