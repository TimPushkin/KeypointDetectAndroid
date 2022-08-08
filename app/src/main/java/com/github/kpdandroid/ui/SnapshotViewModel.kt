package com.github.kpdandroid.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.github.kpdlib.KeypointDetector

class SnapshotViewModel : ViewModel() {
    var keypointDetector: KeypointDetector? by mutableStateOf(null)

    var paintedSnapshot: ImageBitmap? by mutableStateOf(null)
        private set
    var calcTimeMs: Long? by mutableStateOf(null)
        private set

    val painter = SnapshotPainter()

    fun provideSnapshot(snapshot: Bitmap?, keypoints: List<Offset>, calcTimeMs: Long) {
        paintedSnapshot = snapshot?.asImageBitmap()?.also { painter.draw(it, keypoints) }
        this.calcTimeMs = keypointDetector?.run { calcTimeMs }
    }
}
