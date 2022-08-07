package com.github.kpdandroid.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PointMode

class SnapshotPainter(pointColor: Color = Color.Blue, pointWidth: Float = 10f) {
    private val pointPaint = Paint().apply {
        color = pointColor
        strokeWidth = pointWidth
    }

    var pointColor: Color
        get() = pointPaint.color
        set(value) {
            pointPaint.color = value
        }
    var pointWidth: Float
        get() = pointPaint.strokeWidth
        set(value) {
            pointPaint.strokeWidth = value
        }

    fun paint(snapshot: ImageBitmap, points: List<Offset>) {
        Canvas(snapshot).drawPoints(
            pointMode = PointMode.Points,
            points = points,
            paint = pointPaint
        )
    }
}
