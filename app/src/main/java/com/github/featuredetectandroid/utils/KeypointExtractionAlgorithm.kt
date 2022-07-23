package com.github.featuredetectandroid.utils

enum class KeypointExtractionAlgorithm {
    NONE,
    SIFT,
    SURF,
    ORB,
    SUPERPOINT
}

fun algorithmsNamesAsList() = enumValues<KeypointExtractionAlgorithm>().map {
    when (it) {
        KeypointExtractionAlgorithm.NONE -> "None"
        KeypointExtractionAlgorithm.SUPERPOINT -> "SuperPoint"
        else -> it.name
    }
}
