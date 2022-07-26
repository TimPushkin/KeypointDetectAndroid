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

        fun nameToFeatureDetector(
            context: Context,
            algorithmName: String,
            width: Int = 0,
            height: Int = 0
        ) = when (algorithmName) {
            SUPERPOINT.algorithmName -> SuperPoint(context, width, height)
            else -> null
        }
    }
}
