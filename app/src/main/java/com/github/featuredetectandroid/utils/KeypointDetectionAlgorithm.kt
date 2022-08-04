package com.github.featuredetectandroid.utils

import android.content.Context
import com.github.featuredetectlib.learned.SuperPoint
import com.github.featuredetectlib.traditional.Orb
import com.github.featuredetectlib.traditional.Sift
import com.github.featuredetectlib.traditional.Surf

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
            width: Int,
            height: Int
        ) = when (algorithmName) {
            SIFT.algorithmName -> Sift(width, height)
            SURF.algorithmName -> Surf(width, height)
            ORB.algorithmName -> Orb(width, height)
            SUPERPOINT.algorithmName -> SuperPoint.Builder(context, width, height).build()
            else -> null
        }
    }
}
