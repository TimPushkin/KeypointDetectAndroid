package com.github.featuredetectandroid.utils

enum class KeypointExtractionAlgorithm {
    NONE,
    SIFT,
    SURF,
    ORB,
    SUPER_POINT
}

fun stringToAlgorithmMap() = mapOf(
    "None" to KeypointExtractionAlgorithm.NONE,
    "SIFT" to KeypointExtractionAlgorithm.SIFT,
    "SURF" to KeypointExtractionAlgorithm.SURF,
    "ORB" to KeypointExtractionAlgorithm.ORB,
    "SuperPoint" to KeypointExtractionAlgorithm.SUPER_POINT
)
