package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.Descriptor
import com.github.featuredetectlib.FeatureDetector
import com.github.featuredetectlib.Keypoint
import com.github.featuredetectlib.traditional.FeatureDetector as NativeDetector

/**
 * Wrapper for a native feature detector.
 */
internal class NativeDetectorWrapper(private val detector: NativeDetector) : FeatureDetector {
    override var width: Int
        get() = detector.width
        set(value) {
            detector.width = value
        }
    override var height: Int
        get() = detector.height
        set(value) {
            detector.height = value
        }

    override fun detect(image: ByteArray): Pair<List<Keypoint>, List<Descriptor>> {
        val output = detector.detect(image)
        val keypoints = output.keypoints.map { NativeKeypointWrapper(it) }
        val descriptors = output.descriptors.map { descriptor -> descriptor.map { it.toFloat() } }
        return keypoints to descriptors
    }
}
