package com.github.kpdandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.github.kpdlib.KeypointDetector

class OutputViewModel : ViewModel() {
    /**
     * Mutable [Bitmap] to draw keypoints onto and display.
     */
    var frameBitmap: Bitmap? by mutableStateOf(null)
    var keypointOffsets: List<Offset> by mutableStateOf(emptyList())
    var keypointDetector: KeypointDetector? by mutableStateOf(null)
    var calcTimeMs: Long by mutableStateOf(0)
}
