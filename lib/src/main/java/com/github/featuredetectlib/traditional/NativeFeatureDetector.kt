package com.github.featuredetectlib.traditional

import com.github.featuredetectlib.Descriptor
import com.github.featuredetectlib.FeatureDetector
import com.github.featuredetectlib.Keypoint

/**
 * Wrapper for a native feature detector.
 */
class NativeFeatureDetector : FeatureDetector {
    override var width: Int
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")

    override var height: Int
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")

    override fun detect(image: ByteArray): Pair<List<Keypoint>, List<Descriptor>> {
        TODO("Not yet implemented")
    }
}
