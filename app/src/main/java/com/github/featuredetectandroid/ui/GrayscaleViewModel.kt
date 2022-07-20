package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.utils.luminanceArrayToBitmap

class GrayscaleViewModel : ViewModel() {
    var grayscaleBitmap: Bitmap? by mutableStateOf(null)
    var width by mutableStateOf(0)
    var height by mutableStateOf(0)

    fun setPicture(grayscaleByteArray: ByteArray, newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight
        grayscaleBitmap = luminanceArrayToBitmap(
            grayscaleByteArray,
            width,
            height
        )
    }
}
