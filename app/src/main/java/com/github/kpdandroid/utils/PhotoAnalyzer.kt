package com.github.kpdandroid.utils

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.kpdandroid.ui.SnapshotViewModel
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_STEP = 90

class PhotoAnalyzer(private val snapshotViewModel: SnapshotViewModel) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        var width = image.width
        var height = image.height
        var oriented = image.planes[0].buffer.toByteArray()
        Log.v(TAG, "Analyzing snapshot of size $width x $height.")

        // Rotate if needed
        val rotationDegrees = image.imageInfo.rotationDegrees
        Log.v(TAG, "Rotating snapshot on $rotationDegrees degrees.")
        repeat(rotationDegrees / ROTATION_STEP) {
            oriented = rotateRgbaBytes90degreesClockwise(oriented, width, height)
            width = height.also { height = width }
        }

        // Detect keypoints
        val (keypoints, calcTimeMs) = runDetection(rgbaBytesToRgbBytes(oriented), width, height)

        snapshotViewModel.provideSnapshot(
            snapshot = rgbaBytesToBitmap(oriented, width, height),
            keypoints = keypoints.map { Offset(it.x, it.y) },
            calcTimeMs = calcTimeMs
        )

        image.close()
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    private fun runDetection(orientedRgbSnapshot: ByteArray, width: Int, height: Int) =
        snapshotViewModel.keypointDetector?.let { detector ->
            if (detector.width != width) detector.width = width
            if (detector.height != height) detector.height = height

            val startTime = SystemClock.elapsedRealtime()
            val (keypoints, _) = detector.detect(orientedRgbSnapshot)
            val calcTimeMs = SystemClock.elapsedRealtime() - startTime

            Log.v(TAG, "Detected ${keypoints.size} in $calcTimeMs ms.")

            keypoints.map { Offset(it.x, it.y) } to calcTimeMs
        } ?: (emptyList<Offset>() to 0L)
}
