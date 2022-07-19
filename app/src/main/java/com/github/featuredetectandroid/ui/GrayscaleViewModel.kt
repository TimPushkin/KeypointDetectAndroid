package com.github.featuredetectandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.utils.grayscaleByteArrayToBitmap

class GrayscaleViewModel : ViewModel() {
    private var grayscaleByteArray by mutableStateOf(byteArrayOf())
    private var width by mutableStateOf(0)
    private var height by mutableStateOf(0)

    @Suppress("FunctionNaming")
    @Composable
    fun ViewGrayscale() {
        if (width == 0 || height == 0) return
        val grayscaleBitmap = grayscaleByteArrayToBitmap(
            grayscaleByteArray,
            width,
            height
        )
        Image(
            bitmap = grayscaleBitmap.asImageBitmap(),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Grayscale photo"
        )
    }

    fun setPicture(newByteArray: ByteArray, newWidth: Int, newHeight: Int) {
        grayscaleByteArray = newByteArray
        width = newWidth
        height = newHeight
    }
}
