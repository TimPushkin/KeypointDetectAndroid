package com.github.kpdandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.github.kpdlib.KeypointDetector

class ImageAnalysisViewModel : ViewModel() {
    private val painter = KeypointPainter()

    var keypointDetector: KeypointDetector? by mutableStateOf(null)
    var imageLayers: Pair<ImageBitmap, ImageBitmap>? by mutableStateOf(null)
        private set
    var calcTimeMs: Pair<Double, Double>? by mutableStateOf(null)

    fun provideImage(image: ImageBitmap?) {
        if (image == null) {
            painter.updateImage(null)
            imageLayers = null
            return
        }

        val oldKeypointsBitmap = imageLayers?.second?.asAndroidBitmap()
        val requiredSize = image.asAndroidBitmap().byteCount
        val newKeypointsLayer = when {
            oldKeypointsBitmap == null || requiredSize > oldKeypointsBitmap.allocationByteCount ->
                ImageBitmap(image.width, image.height, image.config)
            requiredSize < oldKeypointsBitmap.allocationByteCount ->
                oldKeypointsBitmap.apply { reconfigure(image.width, image.height, config) }
                    .asImageBitmap()
            else -> oldKeypointsBitmap.asImageBitmap() // Sizes are equal
        }

        painter.updateImage(newKeypointsLayer)
        imageLayers = image to newKeypointsLayer
    }

    fun drawKeypoints(keypoints: List<Offset>) {
        painter.draw(keypoints)
    }

    fun setKeypointColor(color: Color) {
        painter.pointColor = color
    }
}
