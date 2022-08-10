package com.github.kpdandroid.utils

import android.graphics.Bitmap
import android.graphics.Color

private const val RGBA_CHANNELS_NUM = 4
private const val RGB_CHANNELS_NUM = 3

/**
 * Converts an array of RGBA pixel data into an array of packed RGB pixel data.
 *
 * The provided pixel data must consist of rows each of which contains pixels in its beginning with
 * optional padding in its end. Each pixel contains its alpha-RGB channels in its beginning with
 * optional padding in its end.
 *
 * @param rgbaBytes RGBA pixels.
 * @param width number of pixels in a row.
 * @param height number of rows.
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

fun bitmapToRgbBytes(bitmap: Bitmap): ByteArray {
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    val rgbBytes = ByteArray(pixels.size * RGB_CHANNELS_NUM)
    pixels.forEachIndexed { index, pixel ->
        val byteIndex = index * RGB_CHANNELS_NUM
        rgbBytes[byteIndex + RED_POS] = Color.red(pixel).toByte()
        rgbBytes[byteIndex + GREEN_POS] = Color.green(pixel).toByte()
        rgbBytes[byteIndex + BLUE_POS] = Color.blue(pixel).toByte()
    }

    return rgbBytes
}

private const val MAX_COLOR = 0xff // Maximum ARGB_8888 channel value

/**
 * Converts an array of RGBA pixel data into a [Bitmap] in [Bitmap.Config.ARGB_8888] format.
 *
 * @param rgbaBytes RGBA pixels in format `[R_1, G_1, B_1, A_1, ..., R_n, G_n, B_n, A_n]`.
 * @param width width of the image.
 * @param height height of the image.
 * @param rowStride size of each row in bytes.
 * @param pixelStride size of each pixel in bytes.
 * @return immutable [Bitmap] in [Bitmap.Config.ARGB_8888] format containing the given pixels.
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

    val pixels = IntArray(width * height)
    for (rowIndex in 0 until height) {
        for (pixelIndex in 0 until width) {
            val byteIndex = rowStride * rowIndex + pixelStride * pixelIndex

            val r = rgbaBytes[byteIndex + RED_POS].toInt() and MAX_COLOR
            val g = rgbaBytes[byteIndex + GREEN_POS].toInt() and MAX_COLOR
            val b = rgbaBytes[byteIndex + BLUE_POS].toInt() and MAX_COLOR
            val a = rgbaBytes[byteIndex + ALPHA_POS].toInt() and MAX_COLOR

            pixels[width * rowIndex + pixelIndex] = Color.argb(a, r, g, b)
        }
    }

    return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
}
