package com.github.kpdandroid.ui.viewmodels

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.github.kpdandroid.KeypointDetectApp
import com.github.kpdandroid.utils.bitmapToRgbBytes
import com.github.kpdandroid.utils.detection.DetectionLogger
import com.github.kpdandroid.utils.detection.detectTimedRepeated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "FileAnalysisViewModel"

private const val DETECTION_PROGRESS_NULLING_DELAY = 200L

class FileAnalysisViewModel(app: Application) :
    ImageAnalysisViewModel(app, { (app as KeypointDetectApp).prefs.fileAlgoTitle }) {
    private var logger: DetectionLogger? = null
        set(value) {
            field?.close()
            field = value
        }

    var calcTimesMs: Pair<Double, Double>? by mutableStateOf(null)

    private var detectionRunningJob: Job = Job().apply { complete() }
    var detectionProgress: Float? by mutableStateOf(null)
        private set

    fun startDetection(times: Int) {
        Log.d(TAG, "Starting detection.")

        val detector = keypointDetector
        if (detector == null) {
            Log.e(TAG, "Cannot run detection: detector is not selected.")
            return
        }
        val image = imageLayers?.first
        if (image == null) {
            Log.e(TAG, "Cannot run detection: image is not selected.")
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            if (!detectionRunningJob.run { isCompleted || isCancelled }) stopDetection()

            Log.i(TAG, "Running detection for $times times.")

            detectionRunningJob = launch {
                detectionProgress = 0.0f

                val rgbBytes = bitmapToRgbBytes(image.asAndroidBitmap())
                val results = detector.detectTimedRepeated(
                    rgbBytes = rgbBytes,
                    imageWidth = image.width,
                    imageHeight = image.height,
                    times = times
                )

                results.forEachIndexed { i, (keypointsWithTime, meanWithError) ->
                    drawKeypoints(keypointsWithTime.first)
                    calcTimesMs = meanWithError
                    detectionProgress = (i + 1).toFloat() / times
                    logger?.log(
                        detector::class.simpleName,
                        image.width,
                        image.height,
                        keypointsWithTime.first.size,
                        keypointsWithTime.second
                    )
                    yield()
                }
                logger?.save()

                delay(DETECTION_PROGRESS_NULLING_DELAY) // Show the final progress for a moment
                detectionProgress = null
            }
        }
    }

    suspend fun stopDetection() {
        Log.i(TAG, "Stopping detection (progress was $detectionProgress).")
        detectionRunningJob.cancelAndJoin()
        logger?.save()
        detectionProgress = null
    }

    fun setupImage(imageInputStream: InputStream?) {
        Log.d(TAG, "Setting up image from $imageInputStream.")
        val image = imageInputStream?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        if (image != null) {
            super.updateMainLayer(image)
        } else {
            Log.e(TAG, "Failed to open and decode image from $imageInputStream.")
        }
    }

    fun setupLogger(logOutputStream: OutputStream?) {
        Log.d(TAG, "Setting up logger to $logOutputStream.")
        if (logOutputStream != null) {
            logger = DetectionLogger(logOutputStream)
        } else {
            logger = null
            Log.e(TAG, "Failed to set up logging to $logOutputStream.")
        }
    }

    override fun onCleared() {
        logger?.close()
        super.onCleared()
    }
}
