package com.github.kpdlib

typealias Descriptor = List<Float>

/**
 * Detects keypoints and their descriptors on an image.
 */
interface KeypointDetector {
    /**
     * Width of the passed images to be processed.
     */
    var width: Int

    /**
     * Height of the passed images to be processed.
     */
    var height: Int

    /**
     * Detects keypoints and their descriptors on an image.
     *
     * Returns keypoints and descriptors so that elements with the same index correspond to each
     * other.
     *
     * @param image array of image information where each pixel is represented as three successive
     * R, G, B bytes in the order in which the pixels appear in the image.
     */
    fun detect(image: ByteArray): Pair<List<Keypoint>, List<Descriptor>>
}
