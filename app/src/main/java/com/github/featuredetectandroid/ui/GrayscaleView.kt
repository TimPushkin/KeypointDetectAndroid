package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
@Suppress("FunctionNaming")
fun GrayscaleView(
    grayscaleBitmap: Bitmap?,
    width: Int,
    height: Int
) {
    if (width == 0 || height == 0) return
    Image(
        bitmap = grayscaleBitmap?.asImageBitmap() ?: return,
        modifier = Modifier.fillMaxSize(),
        contentDescription = "Grayscale photo"
    )
}
