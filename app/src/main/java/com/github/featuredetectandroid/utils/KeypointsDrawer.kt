package com.github.featuredetectandroid.utils

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode

class KeypointsDrawer {
    @Composable
    fun draw(
        modifier: Modifier = Modifier,
        keypoints: List<Offset>?
    ) = keypoints?.let { points ->
        Canvas(modifier = modifier) {
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = Color.Blue,
                strokeWidth = 10f
            )
        }
    }
}
