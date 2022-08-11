package com.github.kpdandroid.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewModelScope
import com.github.kpdandroid.utils.bitmapToRgbBytes
import com.github.kpdandroid.utils.detection.detectTimedRepeated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

private const val TAG = "FileAnalysisViewModel"

private const val DETECTION_PROGRESS_NULLING_DELAY = 200L

class FileAnalysisViewModel : ImageAnalysisViewModel() {
    var calcTimesMs: Pair<Double, Double>? by mutableStateOf(null)

    private var detectionRunningJob: Job = Job().apply { complete() }
    var detectionProgress: Float? by mutableStateOf(null)
        private set

    fun startDetection(times: Int) {
        Log.d(TAG, "Starting detection.")

        if (detectionRunningJob.isCompleted || detectionRunningJob.isCancelled) stopDetection()

        keypointDetector?.let { detector ->
            imageLayers?.let { (image, _) ->
                Log.i(TAG, "Running detection for $times times.")

                detectionRunningJob = viewModelScope.launch(Dispatchers.Default) {
                    detectionProgress = 0.0f

                    val rgbBytes = bitmapToRgbBytes(image.asAndroidBitmap())
                    val results = detector.detectTimedRepeated(
                        rgbBytes = rgbBytes,
                        imageWidth = image.width,
                        imageHeight = image.height,
                        times = times
                    )

                    results.forEachIndexed { i, (keypoints, meanCalcTimeMs, devCalcTimeMs) ->
                        yield()
                        drawKeypoints(keypoints)
                        calcTimesMs = meanCalcTimeMs to devCalcTimeMs
                        detectionProgress = (i + 1).toFloat() / times
                    }

                    delay(DETECTION_PROGRESS_NULLING_DELAY) // Time to notice the final progress

                    detectionProgress = null
                }
            } ?: run { Log.e(TAG, "Cannot run detection: image is not selected.") }
        } ?: run { Log.e(TAG, "Cannot run detection: detector is not selected.") }
    }

    fun stopDetection() {
        Log.i(TAG, "Stopping detection (progress was $detectionProgress).")
        detectionRunningJob.cancel("Stop request received.")
        detectionProgress = null
    }
}
