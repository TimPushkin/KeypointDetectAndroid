package com.github.kpdlib

/**
 * Information about keypoint characteristics used to compare keypoints.
 *
 * Most of the keypoint detection algorithms have constant descriptor size.
 */
typealias Descriptor = List<Float>

/**
 * Detects keypoints and their descriptors on an image.
 */
interface KeypointDetector {
    /**
     * Width of the images to be processed.
     */
    var width: Int

    /**
     * Height of the images to be processed.
     */
    var height: Int

    /**
     * Detects keypoints and their descriptors on an image.
     *
     * Returns keypoints and descriptors so that elements with the same index correspond to each
     * other.
     *
     * @param image array of image data where each pixel is represented as three successive
     * R, G, B bytes in the order in which the pixels appear in the image left to right top to
     * bottom; pixel number must be not less than [width] times [height].
     */
    fun detect(image: ByteArray): Pair<List<Keypoint>, List<Descriptor>>
}
