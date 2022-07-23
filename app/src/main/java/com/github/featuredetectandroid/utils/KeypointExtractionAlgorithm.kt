package com.github.featuredetectandroid.utils

enum class KeypointExtractionAlgorithm {
    NONE,
    SIFT,
    SURF,
    ORB,
    SUPERPOINT
}

fun stringToAlgorithmMap() = mapOf(
    "None" to KeypointExtractionAlgorithm.NONE,
    "SIFT" to KeypointExtractionAlgorithm.SIFT,
    "SURF" to KeypointExtractionAlgorithm.SURF,
    "ORB" to KeypointExtractionAlgorithm.ORB,
    "SuperPoint" to KeypointExtractionAlgorithm.SUPERPOINT
)
