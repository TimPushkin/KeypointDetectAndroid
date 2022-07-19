package com.github.featuredetectandroid

import android.graphics.Bitmap
import android.util.Log
import java.nio.IntBuffer

private const val TAG = "Conversions"

private const val ALPHA_SHIFT = 24 // Alpha channel shift
private const val RED_SHIFT = 16 // Red channel shift
private const val GREEN_SHIFT = 8 // Green channel shift
private const val BLUE_SHIFT = 0 // Blue channel shift

private const val MAX_COLOR = 0xff
private const val MAX_A = MAX_COLOR shl ALPHA_SHIFT

fun grayscaleByteArrayToBitmap(grayscaleByteArray: ByteArray, width: Int, height: Int): Bitmap {
    val byteList = grayscaleByteArray.toList()
    val buffer = IntBuffer.allocate(grayscaleByteArray.size).apply {
        byteList.forEach { byte ->
            val intByte = byte.toInt()
            put(MAX_A or (intByte shl RED_SHIFT) or (intByte shl GREEN_SHIFT) or (intByte shl BLUE_SHIFT))
        }
        rewind()
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        copyPixelsFromBuffer(buffer)
    }
}
