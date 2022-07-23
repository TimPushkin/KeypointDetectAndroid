package com.github.featuredetectandroid.utils

import android.util.Log

private const val TAG = "KeypointExtractionAlgorithm"

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

fun getAlgorithmByName(name: String): KeypointExtractionAlgorithm {
    var algorithm = KeypointExtractionAlgorithm.NONE
    try {
        algorithm = KeypointExtractionAlgorithm.valueOf(name)
    } catch (illegalAlgorithmName: IllegalArgumentException) {
        Log.e(TAG, "Illegal algorithm name: $name. $illegalAlgorithmName")
    }
    return algorithm
}
