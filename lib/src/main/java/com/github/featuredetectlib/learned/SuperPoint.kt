package com.github.featuredetectlib.learned

import android.content.Context
import com.github.featuredetectlib.Descriptor
import com.github.featuredetectlib.FeatureDetector
import com.github.featuredetectlib.Keypoint
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Tensor
import kotlin.math.pow

private const val NETWORK_ASSET = "superpoint.ptl"
private const val DECODER_ASSET = "superpoint_decoder.ptl"

private const val DESCRIPTOR_SIZE = 256

/**
 * Detects learned keypoints and their descriptors using
 * [SuperPoint](https://github.com/magicleap/SuperPointPretrainedNetwork) network.
 */
class SuperPoint(context: Context, width: Int, height: Int) : FeatureDetector {
    private val net = LiteModuleLoader.loadModuleFromAsset(context.assets, NETWORK_ASSET)
    private val decoder = LiteModuleLoader.loadModuleFromAsset(context.assets, DECODER_ASSET)

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
        val output = net.forward(IValue.from(input)).toTuple()
        val decoded = decoder.forward(output[0], output[1], wrappedHeight, wrappedWidth).toTuple()

        val keypoints = keypointTensorToKeypointList(decoded[0].toTensor())
        val descriptors = descriptorTensorToDescriptorList(decoded[1].toTensor())

        return keypoints to descriptors
    }

    @Suppress("MagicNumber")
    private fun rgbToGrayscale(pixels: ByteArray): FloatArray =
        FloatArray(width * height) { i ->
            val r = linearizeSrgbChannel((pixels[3 * i] + 128) / 255f)
            val g = linearizeSrgbChannel((pixels[3 * i + 1] + 128) / 255f)
            val b = linearizeSrgbChannel((pixels[3 * i + 2] + 128) / 255f)
            0.2126f * r + 0.7152f * g + 0.0722f * b
        }

    @Suppress("MagicNumber")
    private fun linearizeSrgbChannel(value: Float): Float =
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
}
