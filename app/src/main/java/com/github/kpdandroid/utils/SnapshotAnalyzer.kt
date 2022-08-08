package com.github.kpdandroid.utils

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.kpdandroid.ui.SnapshotViewModel
import java.nio.ByteBuffer

private const val TAG = "SnapshotAnalyzer"

class SnapshotAnalyzer(private val snapshotViewModel: SnapshotViewModel) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val width = image.width
        val height = image.height
        val snapshot = image.planes[0].buffer.toByteArray()
        Log.v(TAG, "Analyzing snapshot of size ${width}x$height.")

        val (keypoints, calcTimeMs) = runDetection(rgbaBytesToRgbBytes(snapshot), width, height)
        Log.v(TAG, "Detected ${keypoints.size} keypoints in $calcTimeMs ms.")

        snapshotViewModel.provideSnapshot(
            snapshot = rgbaBytesToBitmap(snapshot, width, height),
            keypoints = keypoints.map { Offset(it.x, it.y) },
            calcTimeMs = calcTimeMs
        )

        image.close()
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    private fun runDetection(rgbSnapshot: ByteArray, width: Int, height: Int) =
        snapshotViewModel.keypointDetector?.let { detector ->
            if (detector.width != width) detector.width = width
            if (detector.height != height) detector.height = height

            val startTime = SystemClock.elapsedRealtime()
            val (keypoints, _) = detector.detect(rgbSnapshot)
            val calcTimeMs = SystemClock.elapsedRealtime() - startTime

            keypoints.map { Offset(it.x, it.y) } to calcTimeMs
        } ?: (emptyList<Offset>() to 0L)
}
