package com.github.featuredetectandroid.utils

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.featuredetectandroid.ui.GrayscaleViewModel
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_STEP = 90

class PhotoAnalyzer(private val imageViewModel: GrayscaleViewModel) : ImageAnalysis.Analyzer {
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

    // Rotation of incorrectly oriented images is implemented here.
    @Suppress("ForbiddenComment")
    override fun analyze(image: ImageProxy) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        Log.i(TAG, "The image is rotated on $rotationDegrees degrees.")

        var width = image.width
        var height = image.height
        var oriented = image.planes[0].buffer.toByteArray()

        // TODO: Optimize rotations for 180 and 270 degrees.
        repeat((0 until (rotationDegrees / ROTATION_STEP)).count()) {
            oriented = rotateClockwiseOnRotationStep(oriented, width, height).also {
                width = height.also { height = width }
            }
        }

        imageViewModel.setPicture(oriented, width, height)
        image.close()
    }
}
