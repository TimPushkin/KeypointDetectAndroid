package com.github.featuredetectandroid.utils

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode

class KeypointsDrawer {
    @Composable
    fun Draw(
        modifier: Modifier = Modifier,
        keypointsOffset: List<Offset>
    ) = Canvas(modifier = modifier) {
        drawPoints(
            points = keypointsOffset,
            pointMode = PointMode.Points,
            color = Color.Blue,
            strokeWidth = 10f
        )
    }
}
