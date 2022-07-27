package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.github.featuredetectlib.FeatureDetector

class OutputViewModel : ViewModel() {
    var frameBitmap: Bitmap? by mutableStateOf(null)
    var keypointOffsets: List<Offset> by mutableStateOf(emptyList())
    var featureDetector: FeatureDetector? by mutableStateOf(null)
    var frames: Int by mutableStateOf(0)
    var milliseconds: Long by mutableStateOf(0)
    var isDetectorChanged: Boolean by mutableStateOf(false)
}
