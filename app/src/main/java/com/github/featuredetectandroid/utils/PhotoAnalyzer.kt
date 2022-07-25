package com.github.featuredetectandroid.utils

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.featuredetectandroid.ui.OutputViewModel
import com.github.featuredetectandroid.utils.conversions.luminanceArrayToRGB
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_STEP = 90

class PhotoAnalyzer(private val imageViewModel: OutputViewModel) : ImageAnalysis.Analyzer {
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
        val rotationDegrees = image.imageInfo.rotationDegrees
        val featureDetector = imageViewModel.featureDetector

        var width = image.width
        var height = image.height
        var oriented = image.planes[0].buffer.toByteArray()

        // Rotation of incorrectly oriented images is implemented here.
        // TODO: Optimize rotations for 180 and 270 degrees.
        repeat(rotationDegrees / ROTATION_STEP) {
            Log.i(TAG, "The image is rotated on $rotationDegrees degrees.")
            oriented = rotateClockwiseOnRotationStep(oriented, width, height)
            width = height.also { height = width }
        }

        featureDetector?.width = width
        featureDetector?.height = height
        val keypoints = (
            featureDetector
                ?.detect(luminanceArrayToRGB(oriented)) ?: Pair(
                emptyList(),
                emptyList()
            )
            )
            .first
        imageViewModel.setKeypointsForOutput(keypoints)
        imageViewModel.setPicture(oriented, width, height)
        image.close()
    }
}
