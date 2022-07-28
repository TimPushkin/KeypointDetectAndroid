package com.github.featuredetectandroid.utils

import android.graphics.Bitmap
import android.util.Log
import java.nio.IntBuffer

private const val TAG = "RGBConversions"

private const val ALPHA_SHIFT = 24 // Alpha channel shift
private const val RED_SHIFT = 16 // Red channel shift
private const val GREEN_SHIFT = 8 // Green channel shift
private const val BLUE_SHIFT = 0 // Blue channel shift

private const val RED = 3 // Red position in pixel array
private const val GREEN = 2 // Green position in pixel array
private const val BLUE = 1 // Blue position in pixel array
private const val ALPHA = 0 // Alpha position in pixel array

private const val MAX_COLOR = 0xff

private const val RGB_COMPONENTS = 3
private const val ARGB_COMPONENTS = 4

fun rgbaComponentsToRGBByteArray(imageArray: ByteArray): ByteArray {
    val rgbArray = ByteArray((imageArray.size / ARGB_COMPONENTS) * RGB_COMPONENTS)
    for (pixelIndex in 0 until rgbArray.size / RGB_COMPONENTS) {
        for (componentIndex in 0 until RGB_COMPONENTS) {
            rgbArray[RGB_COMPONENTS * pixelIndex + componentIndex] =
                imageArray[ARGB_COMPONENTS * pixelIndex + componentIndex]
        }
    }
    return rgbArray
}

fun rgbaComponentsToBitmap(imageArray: ByteArray, width: Int, height: Int): Bitmap {
    Log.i(TAG, "Converting ByteArray image of size $width x $height to a Bitmap.")

    val buffer = IntBuffer.allocate(imageArray.size).apply {
        for (pixelIndex in imageArray.indices step ARGB_COMPONENTS) {
            val pixelARGB = Array(ARGB_COMPONENTS) { 0 }
            for (componentIndex in 0 until ARGB_COMPONENTS) {
                val shiftedIndex = (componentIndex + 1) % ARGB_COMPONENTS
                pixelARGB[shiftedIndex] =
                    imageArray[pixelIndex + componentIndex].toInt() and MAX_COLOR
            }
            put(
                (
                    (pixelARGB[RED] shl RED_SHIFT) or (pixelARGB[GREEN] shl GREEN_SHIFT) or
                        (pixelARGB[BLUE] shl BLUE_SHIFT) or (pixelARGB[ALPHA] shl ALPHA_SHIFT)
                    )
            )
        }
        rewind()
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        copyPixelsFromBuffer(buffer)
    }
}
