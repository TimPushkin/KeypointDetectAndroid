package com.github.featuredetectandroid.utils

enum class KeypointExtractionAlgorithm {
    NONE,
    SIFT,
    SURF,
    ORB,
    SUPERPOINT;

    companion object {
        val names = values().map {
            when (it) {
                NONE -> "None"
                SUPERPOINT -> "SuperPoint"
                else -> it.name
            }
        }
    }
}
