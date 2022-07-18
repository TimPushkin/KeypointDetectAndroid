package com.github.featuredetectandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.grayscaleByteArrayToBitmap

class GrayscaleViewModel : ViewModel() {
    private var grayscaleByteArray by mutableStateOf(byteArrayOf())
    private var width by mutableStateOf(0)
    private var height by mutableStateOf(0)

    @Composable
    fun ViewGrayscale() {
        val grayscaleBitmap = grayscaleByteArrayToBitmap(
            grayscaleByteArray,
            width,
            height
        )
        Image(
            bitmap = grayscaleBitmap.asImageBitmap(),
            contentDescription = "Grayscale photo"
        )
    }
    fun setPicture(newByteArray: ByteArray, newWidth: Int, newHeight: Int) {
        grayscaleByteArray = newByteArray
        width = newWidth
        height = newHeight
    }
}
