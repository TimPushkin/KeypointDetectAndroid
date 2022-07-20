package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.utils.luminanceArrayToBitmap

class GrayscaleViewModel : ViewModel() {
    var grayscaleBitmap: Bitmap? by mutableStateOf(null)

    fun setPicture(grayscaleByteArray: ByteArray, newWidth: Int, newHeight: Int) {
        grayscaleBitmap = luminanceArrayToBitmap(
            grayscaleByteArray,
            newWidth,
            newHeight
        )
    }
}
