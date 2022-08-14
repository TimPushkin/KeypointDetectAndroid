package com.github.kpdandroid.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Canvas as NativeCanvas
import android.graphics.Color as NativeColor

private const val KEYPOINT_WIDTH = 10f

class KeypointPainter(image: ImageBitmap? = null, pointColor: Color = Color.Blue) {
    private var bitmap = image?.asAndroidBitmap()
    private val canvas = image?.let { Canvas(it) } ?: Canvas(NativeCanvas())
    private val pointPaint = Paint().apply {
        color = pointColor
        strokeWidth = KEYPOINT_WIDTH
    }

    var pointColor: Color
        get() = pointPaint.color
        set(value) {
            pointPaint.color = value
        }

    fun updateImage(image: ImageBitmap?) {
        image?.asAndroidBitmap().let { bitmap ->
            canvas.nativeCanvas.setBitmap(bitmap)
            this.bitmap = bitmap
        }
    }

    fun draw(points: List<Offset>) {
        bitmap?.eraseColor(NativeColor.TRANSPARENT)
        canvas.drawPoints(
            pointMode = PointMode.Points,
            points = points,
            paint = pointPaint
        )
    }
}
