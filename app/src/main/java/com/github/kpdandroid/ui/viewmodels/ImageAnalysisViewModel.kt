package com.github.kpdandroid.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.github.kpdandroid.ui.KeypointPainter
import com.github.kpdlib.KeypointDetector

private const val TAG = "ImageAnalysisViewModel"

open class ImageAnalysisViewModel : ViewModel() {
    private val painter = KeypointPainter()

    var keypointDetector: KeypointDetector? by mutableStateOf(null)
    var imageLayers: Pair<ImageBitmap, ImageBitmap>? by mutableStateOf(null)
        private set

    fun provideImage(image: ImageBitmap?) {
        if (image == null) {
            painter.updateImage(null)
            imageLayers = null
            return
        }

        val oldPaintBitmap = imageLayers?.second?.asAndroidBitmap()
        val requiredSize = image.asAndroidBitmap().byteCount
        val paintLayer = when {
            oldPaintBitmap == null || requiredSize > oldPaintBitmap.allocationByteCount -> {
                // Cannot reuse the old bitmap
                Log.d(TAG, "Allocating a new bitmap for painting (required size $requiredSize).")
                ImageBitmap(image.width, image.height).also { painter.updateImage(it) }
            }
            oldPaintBitmap.width != image.width && oldPaintBitmap.height != image.height -> {
                // Can reconfigure the old bitmap, but must detach it first
                Log.d(TAG, "Reconfiguring the old painting bitmap.")
                painter.updateImage(null)
                imageLayers = null
                oldPaintBitmap.apply { reconfigure(image.width, image.height, config) }
                    .asImageBitmap()
                    .also { painter.updateImage(it) }
            }
            else -> oldPaintBitmap.asImageBitmap() // Sizes are equal
        }

        imageLayers = image to paintLayer
    }

    fun drawKeypoints(keypoints: List<Offset>) {
        painter.draw(keypoints)
    }

    fun setKeypointColor(color: Color) {
        painter.pointColor = color
    }
}
