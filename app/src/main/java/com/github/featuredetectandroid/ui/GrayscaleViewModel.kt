package com.github.featuredetectandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.featuredetectandroid.grayscaleByteArrayToBitmap

class GrayscaleViewModel : ViewModel() {
    private val grayscaleByteArray: MutableLiveData<ByteArray> = MutableLiveData(byteArrayOf())
    private val width: MutableLiveData<Int> = MutableLiveData(0)
    private val height: MutableLiveData<Int> = MutableLiveData(0)

    @Composable
    fun ViewGrayscale() {
        val grayscaleByteArrayValue: ByteArray = grayscaleByteArray.value ?: return
        val widthValue: Int = width.value ?: return
        val heightValue: Int = height.value ?: return
        val grayscaleBitmap = grayscaleByteArrayToBitmap(grayscaleByteArrayValue, widthValue, heightValue)
        Image(
            bitmap = grayscaleBitmap.asImageBitmap(),
            contentDescription = "Grayscale photo"
        )
    }
    fun setPicture(newByteArray: ByteArray, newWidth: Int, newHeight: Int) {
        grayscaleByteArray.value = newByteArray
        width.value = newWidth
        height.value = newHeight
    }
}
