package com.github.kpdandroid.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.kpdandroid.KeypointDetectApp
import com.github.kpdandroid.ui.KeypointPainter
import com.github.kpdandroid.utils.detection.DetectionAlgo
import com.github.kpdlib.KeypointDetector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val TAG = "ImageAnalysisViewModel"

abstract class ImageAnalysisViewModel(app: Application, algoTitleGetter: () -> String) :
    AndroidViewModel(app) {
    val prefs = (app as KeypointDetectApp).prefs
    private val painter = KeypointPainter()

    var keypointDetector: KeypointDetector? = null
        private set
    var imageLayers: Pair<ImageBitmap, ImageBitmap>? by mutableStateOf(null)
        private set

    var keypointColor: Color
        get() = painter.pointColor
        set(value) {
            painter.pointColor = value
        }

    init {
        snapshotFlow(algoTitleGetter)
            .distinctUntilChanged()
            .onEach { newAlgoTitle ->
                keypointDetector = DetectionAlgo.constructDetectorFrom(
                    algoTitle = newAlgoTitle,
                    context = getApplication(),
                    width = keypointDetector?.width ?: 0,
                    height = keypointDetector?.height ?: 0
                )
            }.launchIn(viewModelScope)
    }

    protected fun updateMainLayer(image: ImageBitmap?) {
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
}
