package com.github.featuredetectandroid.utils

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.featuredetectandroid.ui.GrayscaleViewModel
import java.nio.ByteBuffer

private const val TAG = "PhotoAnalyzer"
private const val ROTATION_ON_90_DEGREES = 90

class PhotoAnalyzer(private val imageViewModel: GrayscaleViewModel) : ImageAnalysis.Analyzer {
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    private fun rotateRightOn90Deg(
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

    /* Here is the 0 or 90 deg (depending on state) right rotation hardcoded
    because of wrong orientation of some images got from camera. */
    override fun analyze(image: ImageProxy) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        Log.i(TAG, "The image is rotated on $rotationDegrees degrees.")

        var width = image.width
        var height = image.height
        val buffer = image.planes[0].buffer
        val grayScaleByteArray = buffer.toByteArray()
        val oriented = if (rotationDegrees == ROTATION_ON_90_DEGREES) {
            rotateRightOn90Deg(grayScaleByteArray, width, height).also {
                width = height.also { height = width }
            }
        } else {
            grayScaleByteArray
        }
        imageViewModel.setPicture(oriented, width, height)
        image.close()
    }
}
