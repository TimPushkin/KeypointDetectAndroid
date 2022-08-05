package com.github.kpdandroid.utils

import android.graphics.Bitmap
import java.nio.IntBuffer

private const val RGB_COMPONENTS_NUM = 3
private const val RGBA_COMPONENTS_NUM = 4

/**
 * Converts an array of RGBA pixel data into an array of RGB pixel data.
 *
 * @param imageArray RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @return RGB pixels in format `[R_1, G_1, B_1, ..., R_n, G_n, B_n]`.
 */
fun rgbaComponentsToRgbByteArray(imageArray: ByteArray): ByteArray {
    val pixelsNum = imageArray.size / RGBA_COMPONENTS_NUM
    val rgbArray = ByteArray(pixelsNum * RGB_COMPONENTS_NUM)
    for (pixelIndex in 0 until pixelsNum) {
        for (componentIndex in 0 until RGB_COMPONENTS_NUM) {
            rgbArray[RGB_COMPONENTS_NUM * pixelIndex + componentIndex] =
                imageArray[RGBA_COMPONENTS_NUM * pixelIndex + componentIndex]
        }
    }
    return rgbArray
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
 * @param imageArray RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @param width width of the image.
 * @param height height of the image.
 * @return mutable [Bitmap] in [Bitmap.Config.ARGB_8888] format containing the given pixels.
 */
fun rgbaComponentsToBitmap(imageArray: ByteArray, width: Int, height: Int): Bitmap {
    val buffer = IntBuffer.allocate(imageArray.size / RGBA_COMPONENTS_NUM).apply {
        for (pixelIndex in imageArray.indices step RGBA_COMPONENTS_NUM) {
            val r = imageArray[pixelIndex + RED_POS].toInt() and MAX_COLOR
            val g = imageArray[pixelIndex + GREEN_POS].toInt() and MAX_COLOR
            val b = imageArray[pixelIndex + BLUE_POS].toInt() and MAX_COLOR
            val a = imageArray[pixelIndex + ALPHA_POS].toInt() and MAX_COLOR
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
