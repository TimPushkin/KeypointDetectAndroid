package com.github.featuredetectandroid.utils

import androidx.compose.ui.geometry.Offset
import com.github.featuredetectlib.Keypoint

fun keypointsToOffsetList(keypoints: List<Keypoint>) = keypoints.map { Offset(it.x, it.y) }
