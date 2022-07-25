package com.github.featuredetectandroid.utils.conversions

import androidx.compose.ui.geometry.Offset
import com.github.featuredetectlib.Keypoint

fun keypointsToOffsetList(keypoints: List<Keypoint>) = keypoints.map { Offset(it.x, it.y) }
