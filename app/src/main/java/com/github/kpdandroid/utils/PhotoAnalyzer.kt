package com.github.kpdandroid.utils

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.github.kpdandroid.ui.OutputViewModel
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_STEP = 90
private const val RGBA_COMPONENTS_NUM = 4

class PhotoAnalyzer(private val outputViewModel: OutputViewModel) : ImageAnalysis.Analyzer {
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    private fun rotateClockwiseOnRotationStep(
        imageArray: ByteArray,
        width: Int,
        height: Int
    ) = ByteArray(RGBA_COMPONENTS_NUM * width * height).also { array ->
        for (i in 0 until width) {
            for (j in 0 until height) {
                for (componentIndex in 0 until RGBA_COMPONENTS_NUM) {
                    array[RGBA_COMPONENTS_NUM * (i * height + j) + componentIndex] =
                        imageArray[
                            RGBA_COMPONENTS_NUM * (i + (height - 1 - j) * width) + componentIndex
                        ]
                }
            }
        }
    }

    override fun analyze(image: ImageProxy) {
        var width = image.width
        var height = image.height
        var oriented = image.planes[0].buffer.toByteArray()

        // Rotation of incorrectly oriented images is implemented here.
        val rotationDegrees = image.imageInfo.rotationDegrees
        repeat(rotationDegrees / ROTATION_STEP) {
            Log.v(TAG, "The image is rotated on $rotationDegrees degrees.")
            oriented = rotateClockwiseOnRotationStep(oriented, width, height)
            width = height.also { height = width }
        }

        outputViewModel.keypointOffsets =
            outputViewModel.keypointDetector?.let { detector ->
                if (detector.width != width) detector.width = width
                if (detector.height != height) detector.height = height
                val rgbArray = rgbaComponentsToRgbByteArray(oriented)
                val startTime = SystemClock.elapsedRealtime()
                val (keypoints, _) = detector.detect(rgbArray)
                outputViewModel.calcTimeMs = SystemClock.elapsedRealtime() - startTime
                keypoints.map { Offset(it.x, it.y) }
            } ?: emptyList()

        outputViewModel.frameBitmap = rgbaComponentsToBitmap(oriented, width, height)
        image.close()
    }
}
