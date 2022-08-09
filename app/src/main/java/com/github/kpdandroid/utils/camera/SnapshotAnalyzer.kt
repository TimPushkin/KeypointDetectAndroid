package com.github.kpdandroid.utils.camera

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.kpdandroid.ui.viewmodels.CameraAnalysisViewModel
import com.github.kpdandroid.utils.rgbaBytesToBitmap
import com.github.kpdandroid.utils.rgbaBytesToRgbBytes
import java.nio.ByteBuffer

private const val TAG = "SnapshotAnalyzer"

class SnapshotAnalyzer(private val viewModel: CameraAnalysisViewModel) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val width = image.width
        val height = image.height
        val (snapshot, rowStride, pixelStride) =
            image.planes[0].run { Triple(buffer.toByteArray(), rowStride, pixelStride) }
        Log.v(
            TAG,
            "Analyzing snapshot:\n" +
                "- size: ${width}x$height\n" +
                "- pixel stride: $pixelStride\n" +
                "- row stride: $rowStride\n" +
                "- width * pixelStride: ${width * pixelStride}"
        )

        val (keypoints, calcTimeMs) = runDetection(
            rgbSnapshot = rgbaBytesToRgbBytes(snapshot, width, height, rowStride, pixelStride),
            width = width,
            height = height
        )
        Log.v(TAG, "Detected ${keypoints.size} keypoints in $calcTimeMs ms.")

        viewModel.provideSnapshot(
            snapshot = rgbaBytesToBitmap(snapshot, width, height, rowStride, pixelStride),
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
        viewModel.keypointDetector?.let { detector ->
            if (detector.width != width) detector.width = width
            if (detector.height != height) detector.height = height

            val startTime = SystemClock.elapsedRealtime()
            val (keypoints, _) = detector.detect(rgbSnapshot)
            val calcTimeMs = SystemClock.elapsedRealtime() - startTime

            keypoints.map { Offset(it.x, it.y) } to calcTimeMs
        } ?: (emptyList<Offset>() to 0L)
}
