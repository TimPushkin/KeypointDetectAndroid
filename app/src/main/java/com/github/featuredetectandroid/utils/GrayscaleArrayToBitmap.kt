package com.github.featuredetectandroid.utils

import android.graphics.Bitmap
import android.util.Log
import java.nio.IntBuffer

private const val TAG = "GrayscaleArrayToBitmap"

private const val ALPHA_SHIFT = 24 // Alpha channel shift
private const val RED_SHIFT = 16 // Red channel shift
private const val GREEN_SHIFT = 8 // Green channel shift
private const val BLUE_SHIFT = 0 // Blue channel shift

private const val Y_SHIFT = 127 // Y-component should be inverted and shifted

private const val MAX_COLOR = 0xff
private const val MAX_ALPHA = MAX_COLOR shl ALPHA_SHIFT

fun grayscaleArrayToBitmap(grayscaleByteArray: ByteArray, width: Int, height: Int): Bitmap {
    val byteList = grayscaleByteArray.toList()
    Log.i(TAG, "Converting ByteArray image of size $width x $height to a Bitmap.")
    val buffer = IntBuffer.allocate(grayscaleByteArray.size).apply {
        for (byte in byteList) {
            val intByte = Y_SHIFT - byte.toInt()
            put(
                MAX_ALPHA or (intByte shl RED_SHIFT)
                    or (intByte shl GREEN_SHIFT) or (intByte shl BLUE_SHIFT)
            )
        }
        rewind()
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        copyPixelsFromBuffer(buffer)
    }
}
