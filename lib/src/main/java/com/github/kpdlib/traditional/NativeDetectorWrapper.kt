package com.github.kpdlib.traditional

import com.github.kpdlib.Descriptor
import com.github.kpdlib.Keypoint
import com.github.kpdlib.KeypointDetector
import com.github.kpdlib.traditional.KeypointDetector as NativeDetector

/**
 * Wrapper for a native keypoint detector.
 */
internal class NativeDetectorWrapper(private val detector: NativeDetector) : KeypointDetector {
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
