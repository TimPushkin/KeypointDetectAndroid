package com.github.kpdandroid.utils

import android.graphics.Bitmap
import java.nio.IntBuffer

private const val RGBA_CHANNELS_NUM = 4

fun rotateRgbaBytes90degreesClockwise(rgbaBytes: ByteArray, width: Int, height: Int) =
    ByteArray(RGBA_CHANNELS_NUM * width * height).also { array ->
        for (i in 0 until width) {
            for (j in 0 until height) {
                for (channelIndex in 0 until RGBA_CHANNELS_NUM) {
                    array[RGBA_CHANNELS_NUM * (i * height + j) + channelIndex] =
                        rgbaBytes[RGBA_CHANNELS_NUM * (i + (height - 1 - j) * width) + channelIndex]
                }
            }
        }
    }

private const val RGB_CHANNELS_NUM = 3

/**
 * Converts an array of RGBA pixel data into an array of RGB pixel data.
 *
 * @param rgbaBytes RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @return RGB pixels in format `[R_1, G_1, B_1, ..., R_n, G_n, B_n]`.
 */
fun rgbaBytesToRgbBytes(rgbaBytes: ByteArray): ByteArray {
    val pixelsNum = rgbaBytes.size / RGBA_CHANNELS_NUM
    val rgbBytes = ByteArray(pixelsNum * RGB_CHANNELS_NUM)
    for (pixelIndex in 0 until pixelsNum) {
        for (channelIndex in 0 until RGB_CHANNELS_NUM) {
            rgbBytes[RGB_CHANNELS_NUM * pixelIndex + channelIndex] =
                rgbaBytes[RGBA_CHANNELS_NUM * pixelIndex + channelIndex]
        }
    }
    return rgbBytes
}

private const val RED_POS = 0 // Red channel position in RGBA pixel array
private const val GREEN_POS = 1 // Green channel position in RGBA pixel array
private const val BLUE_POS = 2 // Blue channel position in RGBA pixel array
private const val ALPHA_POS = 3 // Alpha channel position in RGBA pixel array

private const val ALPHA_BIT_SHIFT = 24 // Alpha channel shift in ARGB_8888
private const val BLUE_BIT_SHIFT = 16 // Blue channel shift in ARGB_8888
private const val GREEN_BIT_SHIFT = 8 // Green channel shift in ARGB_8888
private const val RED_BIT_SHIFT = 0 // Red channel shift in ARGB_8888

private const val MAX_COLOR = 0xff // Maximum ARGB_8888 channel value

/**
 * Converts an array of RGBA pixel data into a [Bitmap] in [Bitmap.Config.ARGB_8888] format.
 *
 * @param rgbaBytes RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @param width width of the image.
 * @param height height of the image.
 * @return mutable [Bitmap] in [Bitmap.Config.ARGB_8888] format containing the given pixels.
 */
fun rgbaBytesToBitmap(rgbaBytes: ByteArray, width: Int, height: Int): Bitmap {
    val buffer = IntBuffer.allocate(rgbaBytes.size / RGBA_CHANNELS_NUM).apply {
        for (pixelIndex in rgbaBytes.indices step RGBA_CHANNELS_NUM) {
            val r = rgbaBytes[pixelIndex + RED_POS].toInt() and MAX_COLOR
            val g = rgbaBytes[pixelIndex + GREEN_POS].toInt() and MAX_COLOR
            val b = rgbaBytes[pixelIndex + BLUE_POS].toInt() and MAX_COLOR
            val a = rgbaBytes[pixelIndex + ALPHA_POS].toInt() and MAX_COLOR
            // ARGB_8888 pixel is stored in ABGR channel order
            put(
                (a shl ALPHA_BIT_SHIFT) or (b shl BLUE_BIT_SHIFT) or
                    (g shl GREEN_BIT_SHIFT) or (r shl RED_BIT_SHIFT)
            )
        }
        rewind()
    }
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        copyPixelsFromBuffer(buffer)
    }
}
