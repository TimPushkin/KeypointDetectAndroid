package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.utils.luminanceArrayToBitmap
import com.github.featuredetectlib.FeatureDetector

class OutputViewModel : ViewModel() {
    var grayscaleBitmap: Bitmap? by mutableStateOf(null)
    var isCameraPermissionGranted by mutableStateOf(false)
    var keypointOffsets: List<Offset> by mutableStateOf(emptyList())
    var featureDetector: FeatureDetector? by mutableStateOf(null)
    var width by mutableStateOf(0)
    var height by mutableStateOf(0)

    fun setPicture(grayscaleByteArray: ByteArray) {
        grayscaleBitmap = luminanceArrayToBitmap(grayscaleByteArray, width, height)
    }
}
