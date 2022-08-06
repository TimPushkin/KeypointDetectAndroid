package com.github.kpdlib.learned

import android.content.Context
import com.github.kpdlib.Descriptor
import com.github.kpdlib.Keypoint
import com.github.kpdlib.KeypointDetector
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import kotlin.math.pow

private const val NETWORK_ASSET = "superpoint.ptl"
private const val DECODER_ASSET = "superpoint_decoder.ptl"

private const val DESCRIPTOR_SIZE = 256

/**
 * Detects learned keypoints and their descriptors using
 * [SuperPoint](https://github.com/magicleap/SuperPointPretrainedNetwork) pretrained network.
 *
 * - Keypoints don't have sizes and angles
 * - Descriptor size is 256
 *
 * To obtain an instance, use [SuperPoint.Builder].
 */
class SuperPoint private constructor(
    private val net: Module,
    private val decoder: Module,
    width: Int,
    height: Int,
) : KeypointDetector {
    override var width = width
        set(value) {
            field = value
            longWidth = value.toLong()
            wrappedWidth = IValue.from(longWidth)
        }
    private var longWidth = width.toLong()
    private var wrappedWidth = IValue.from(longWidth)

    override var height = height
        set(value) {
            field = value
            longHeight = value.toLong()
            wrappedHeight = IValue.from(longHeight)
        }
    private var longHeight = height.toLong()
    private var wrappedHeight = IValue.from(longHeight)

    override fun detect(image: ByteArray): Pair<List<Keypoint>, List<Descriptor>> {
        val input = Tensor.fromBlob(rgbToGrayscale(image), longArrayOf(1, 1, longHeight, longWidth))
        val (encKeypoints, encDescriptors) = net.forward(IValue.from(input)).toTuple()
        val (decKeypoints, decDescriptors) =
            decoder.forward(encKeypoints, encDescriptors, wrappedHeight, wrappedWidth).toTuple()

        val keypoints = keypointTensorToKeypointList(decKeypoints.toTensor())
        val descriptors = descriptorTensorToDescriptorList(decDescriptors.toTensor())

        return keypoints to descriptors
    }

    /**
     * Converts an array of RGB pixels represented as `[R_1, G_1, B_1, ..., R_n, G_n, B_n]` to a
     * relative luminance array `[L_1, ..., L_n]` as described in
     * [WCAG 2.x](https://www.w3.org/WAI/GL/wiki/Relative_luminance).
     */
    @Suppress("MagicNumber")
    private fun rgbToGrayscale(pixels: ByteArray) =
        FloatArray(width * height) { i ->
            val r = linearizeSrgbChannel((pixels[3 * i] + 128) / 255f)
            val g = linearizeSrgbChannel((pixels[3 * i + 1] + 128) / 255f)
            val b = linearizeSrgbChannel((pixels[3 * i + 2] + 128) / 255f)
            0.2126f * r + 0.7152f * g + 0.0722f * b
        }

    @Suppress("MagicNumber")
    private fun linearizeSrgbChannel(value: Float) =
        if (value <= 0.04045) value / 12.92f else ((value + 0.055f) / 1.055f).pow(2.4f)

    private fun keypointTensorToKeypointList(tensor: Tensor): List<Keypoint> {
        val data = tensor.dataAsFloatArray
        val keyPointsNum = tensor.shape()[1].toInt()
        val list =
            List(keyPointsNum) { i ->
                LearnedKeypoint(
                    x = data[i],
                    y = data[keyPointsNum + i],
                    strength = data[2 * keyPointsNum + i]
                )
            }
        return list
    }

    private fun descriptorTensorToDescriptorList(tensor: Tensor): List<Descriptor> {
        val data = tensor.dataAsFloatArray
        val descriptorsNum = tensor.shape()[1].toInt()
        val list =
            List(descriptorsNum) { j ->
                List(DESCRIPTOR_SIZE) { i ->
                    data[descriptorsNum * i + j]
                }
            }
        return list
    }

    /**
     * Builds [SuperPoint] from [Context].
     *
     * The Context is not retained in the resulting SuperPoint instance.
     */
    class Builder(
        context: Context,
        /**
         * Initial value of [SuperPoint.width].
         */
        var width: Int = 0,
        /**
         * Initial value of [SuperPoint.height].
         */
        var height: Int = 0
    ) {
        private val net = LiteModuleLoader.loadModuleFromAsset(context.assets, NETWORK_ASSET)
        private val decoder = LiteModuleLoader.loadModuleFromAsset(context.assets, DECODER_ASSET)

        /**
         * Sets the initial value of [SuperPoint.width].
         */
        fun width(value: Int) = apply { width = value }

        /**
         * Sets the initial value of [SuperPoint.height].
         */
        fun height(value: Int) = apply { height = value }

        /**
         * Builds a [SuperPoint] instance.
         */
        fun build() = SuperPoint(net, decoder, width, height)
    }
}
