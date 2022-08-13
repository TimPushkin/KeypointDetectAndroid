package com.github.kpdandroid.utils.detection

import android.os.SystemClock
import androidx.compose.ui.geometry.Offset
import com.github.kpdlib.KeypointDetector
import kotlin.math.pow
import kotlin.math.sqrt

typealias KeypointsWithTime = Pair<List<Offset>, Long>

/**
 * Runs keypoint detection on the provided image, changing detector's size if needed. Returns the
 * resulting keypoints and the time the detection took.
 */
fun KeypointDetector.detectTimed(
    rgbBytes: ByteArray,
    imageWidth: Int,
    imageHeight: Int
): KeypointsWithTime {
    if (width != imageWidth) width = imageWidth
    if (height != imageHeight) height = imageHeight

    val startTime = SystemClock.elapsedRealtime()
    val (keypoints, _) = detect(rgbBytes)
    val calcTimeMs = SystemClock.elapsedRealtime() - startTime

    return keypoints.map { Offset(it.x, it.y) } to calcTimeMs
}

typealias MeanWithError = Pair<Double, Double>

/**
 * Creates a sequence of detection calls (each time changing detector's size if needed) measuring
 * mean and standard deviation of the calculation time.
 *
 * Returns the sequence of the latest calculated keypoints with current mean and standard deviation
 * values.
 */
fun KeypointDetector.detectTimedRepeated(
    rgbBytes: ByteArray,
    imageWidth: Int,
    imageHeight: Int,
    times: Int
): Sequence<Pair<KeypointsWithTime, MeanWithError>> = sequence {
    var mean = 0.0
    var dev = 0.0 // Unbiased estimation of standard deviation

    repeat(times) { n ->
        val (keypoints, time) = detectTimed(rgbBytes, imageWidth, imageHeight)

        val oldMean = mean

        // Combined mean: m = ((n1 * m1) + (n2 * m2)) / (n1 + n2)
        //
        // Where:
        // n1, n2 -- numbers of elements in each set
        // m1, m2 -- means of each set
        mean = (n * oldMean + time) / (n + 1)

        // Combined unbiased estimation of standard deviation:
        // https://math.stackexchange.com/a/2971563/1085312
        //
        // d = sqrt(
        //     ((n1 - 1) * d1^2 + (n2 - 1) * d2^2) / (n1 + n2 - 1)
        //     +
        //     (n1 * n2 * (m1 - m2)^2) / ((n1 + n2) * (n1 + n2 - 1))
        // )
        //
        // Where:
        // n1, n2 -- numbers of elements in each set (n1 + n2 > 1)
        // m1, m2 -- means of each set
        // d1, d2 -- unbiased estimations of standard deviation of each set
        dev = if (n > 0) {
            sqrt((n - 1) * dev.pow(2) / n + (oldMean - time).pow(2) / (n + 1))
        } else {
            0.0
        }

        yield((keypoints to time) to (mean to dev))
    }
}
