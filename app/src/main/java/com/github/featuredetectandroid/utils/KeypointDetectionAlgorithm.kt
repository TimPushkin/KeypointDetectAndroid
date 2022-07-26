package com.github.featuredetectandroid.utils

import com.github.featuredetectlib.learned.SuperPoint

enum class KeypointDetectionAlgorithm(
    val algorithmName: String
) {
    NONE("None"),
    SIFT("SIFT"),
    SURF("SURF"),
    ORB("ORB"),
    SUPERPOINT("SuperPoint");

    companion object {
        val names = values().map { it.algorithmName }

        fun nameToClassConstructor(algorithmName: String) = when (algorithmName) {
            SUPERPOINT.algorithmName -> ::SuperPoint
            else -> null
        }
    }
}
