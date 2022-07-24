package com.github.featuredetectandroid.utils

import android.content.Context
import com.github.featuredetectlib.learned.SuperPoint

// TODO: add other FeatureDetector's after their implementation
fun selectFeatureDetector(
    context: Context,
    algorithmName: String,
    width: Int,
    height: Int
) = when (algorithmName) {
    "SuperPoint" -> SuperPoint(context, width, height)
    else -> null
}
