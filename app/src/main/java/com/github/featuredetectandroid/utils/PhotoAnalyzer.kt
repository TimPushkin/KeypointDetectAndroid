package com.github.featuredetectandroid.utils

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.featuredetectandroid.ui.OutputViewModel
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_STEP = 90

class PhotoAnalyzer(private val outputViewModel: OutputViewModel) : ImageAnalysis.Analyzer {
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    private fun rotateClockwiseOnRotationStep(
        grayscaleByteArray: ByteArray,
        width: Int,
        height: Int
    ) = ByteArray(width * height).also { array ->
        for (i in 0 until width) {
            for (j in 0 until height) {
                array[i * height + j] = grayscaleByteArray[i + (height - 1 - j) * width]
            }
        }
    }

    override fun analyze(image: ImageProxy) {
        var width = image.width
        var height = image.height
        var oriented = image.planes[0].buffer.toByteArray()

        // Rotation of incorrectly oriented images is implemented here.
        // TODO: Optimize rotations for 180 and 270 degrees.
        val rotationDegrees = image.imageInfo.rotationDegrees
        repeat(rotationDegrees / ROTATION_STEP) {
            Log.i(TAG, "The image is rotated on $rotationDegrees degrees.")
            oriented = rotateClockwiseOnRotationStep(oriented, width, height)
            width = height.also { height = width }
        }

        outputViewModel.keypointOffsets =
            outputViewModel.featureDetector?.let { detector ->
                if (detector.width != width) detector.width = width
                if (detector.height != height) detector.height = height
                val startTime = SystemClock.elapsedRealtime()
                val (keypoints, _) = detector.detect(luminanceArrayToRGB(oriented))
                outputViewModel.milliseconds = SystemClock.elapsedRealtime() - startTime
                keypoints.map { Offset(it.x, it.y) }
            } ?: emptyList()

        outputViewModel.frameBitmap = luminanceArrayToBitmap(oriented, width, height)

        image.close()
    }
}
