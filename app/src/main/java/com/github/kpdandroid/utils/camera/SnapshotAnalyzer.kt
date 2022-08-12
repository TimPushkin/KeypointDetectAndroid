package com.github.kpdandroid.utils.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.kpdandroid.utils.detection.detectTimed
import com.github.kpdandroid.utils.rgbaBytesToBitmap
import com.github.kpdandroid.utils.rgbaBytesToRgbBytes
import com.github.kpdlib.KeypointDetector
import java.nio.ByteBuffer

private const val TAG = "SnapshotAnalyzer"

class SnapshotAnalyzer(
    private val detectorGetter: () -> KeypointDetector?,
    private val onResult: (snapshot: Bitmap, keypoints: List<Offset>, calcTimeMs: Long) -> Unit
) : ImageAnalysis.Analyzer {
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

        val (keypoints, calcTimeMs) = detectorGetter()?.detectTimed(
            rgbBytes = rgbaBytesToRgbBytes(snapshot, width, height, rowStride, pixelStride),
            imageWidth = width,
            imageHeight = height
        ) ?: (emptyList<Offset>() to 0L)
        Log.v(TAG, "Detected ${keypoints.size} keypoints in $calcTimeMs ms.")

        onResult(
            rgbaBytesToBitmap(snapshot, width, height, rowStride, pixelStride),
            keypoints,
            calcTimeMs
        )

        image.close()
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }
}
