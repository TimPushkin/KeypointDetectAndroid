package com.github.featuredetectandroid.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role.Companion.Image
import com.github.featuredetectandroid.grayscaleByteArrayToBitmap

@Composable
fun ViewGrayscale(grayscaleByteArray: ByteArray, width: Int, height: Int) {
    val grayscaleBitmap = grayscaleByteArrayToBitmap(grayscaleByteArray, width, height)
    Image(
        bitmap = grayscaleBitmap.asImageBitmap(),
        contentDescription = "Grayscale photo"
    )
}
