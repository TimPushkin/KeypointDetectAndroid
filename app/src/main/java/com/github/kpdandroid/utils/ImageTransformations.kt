package com.github.kpdandroid.utils

import android.graphics.Bitmap
import java.nio.IntBuffer

private const val RGBA_CHANNELS_NUM = 4
private const val RGB_CHANNELS_NUM = 3

/**
 * Converts an array of RGBA pixel data into an array of RGB pixel data.
 *
 * @param rgbaBytes RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @param width width of the image.
 * @param height height of the image.
 * @param rowStride size of each row in bytes.
 * @param pixelStride size of each pixel in bytes.
 * @return RGB pixels in format `[R_1, G_1, B_1, ..., R_n, G_n, B_n]`.
 *
 * @throws IllegalArgumentException if rowStride is less than (image width) * (pixel stride) or
 * pixelStride is less than the number of channels in RGBA pixel (i.e. less than 4).
 */
fun rgbaBytesToRgbBytes(
    rgbaBytes: ByteArray,
    width: Int,
    height: Int,
    rowStride: Int,
    pixelStride: Int
): ByteArray {
    if (pixelStride < RGBA_CHANNELS_NUM) {
        throw IllegalArgumentException(
            "Pixel stride $pixelStride is less than pixel size $RGBA_CHANNELS_NUM."
        )
    }
    if (rowStride < width * pixelStride) {
        throw IllegalArgumentException(
            "Row stride $rowStride is less than width * pixel stride = ${width * pixelStride}."
        )
    }

    val rgbBytes = ByteArray(width * height * RGB_CHANNELS_NUM)
    for (rowIndex in 0 until height) {
        for (pixelIndex in 0 until width) {
            for (channelIndex in 0 until RGB_CHANNELS_NUM) {
                rgbBytes[RGB_CHANNELS_NUM * (width * rowIndex + pixelIndex) + channelIndex] =
                    rgbaBytes[rowStride * rowIndex + pixelStride * pixelIndex + channelIndex]
            }
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
 * @param rowStride size of each row in bytes.
 * @param pixelStride size of each pixel in bytes.
 * @return mutable [Bitmap] in [Bitmap.Config.ARGB_8888] format containing the given pixels.
 *
 * @throws IllegalArgumentException if rowStride is less than (image width) * (pixel stride) or
 * pixelStride is less than the number of channels in RGBA pixel (i.e. less than 4).
 */
fun rgbaBytesToBitmap(
    rgbaBytes: ByteArray,
    width: Int,
    height: Int,
    rowStride: Int,
    pixelStride: Int
): Bitmap {
    if (pixelStride < RGBA_CHANNELS_NUM) {
        throw IllegalArgumentException(
            "Pixel stride $pixelStride is less than pixel size $RGBA_CHANNELS_NUM."
        )
    }
    if (rowStride < width * pixelStride) {
        throw IllegalArgumentException(
            "Row stride $rowStride is less than width * pixel stride = ${width * pixelStride}."
        )
    }

    val usefulRowSize = width * pixelStride

    val buffer = IntBuffer.allocate(width * height * RGBA_CHANNELS_NUM).apply {
        for (rowShift in rgbaBytes.indices step rowStride) {
            for (pixelShift in 0 until usefulRowSize step pixelStride) {
                val r = rgbaBytes[rowShift + pixelShift + RED_POS].toInt() and MAX_COLOR
                val g = rgbaBytes[rowShift + pixelShift + GREEN_POS].toInt() and MAX_COLOR
                val b = rgbaBytes[rowShift + pixelShift + BLUE_POS].toInt() and MAX_COLOR
                val a = rgbaBytes[rowShift + pixelShift + ALPHA_POS].toInt() and MAX_COLOR
                // ARGB_8888 pixel is stored in ABGR channel order
                put(
                    (a shl ALPHA_BIT_SHIFT) or (b shl BLUE_BIT_SHIFT) or
                        (g shl GREEN_BIT_SHIFT) or (r shl RED_BIT_SHIFT)
                )
            }
        }
        rewind()
    }
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        copyPixelsFromBuffer(buffer)
    }
}
