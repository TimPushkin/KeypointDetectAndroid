package com.github.kpdandroid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import kotlin.math.abs

private const val TAG = "CameraHandler"

// JPEG is always supported and seems to give the right resolutions for CameraX
private const val IMAGE_FORMAT_CAMERA2 = ImageFormat.JPEG

// Convenient to convert to RGB and Bitmap
private const val IMAGE_FORMAT_CAMERAX = ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888

private const val DEFAULT_SCREEN_ROTATION = 0
private const val ROTATION_SAVING_STEP = 180

class CameraHandler(
    private val activity: ComponentActivity,
    var analyzer: SnapshotAnalyzer,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    private val cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val screenRotation = extractScreenRotation()
    var cameraSelector = cameraSelector
        set(value) {
            supportedResolutions = emptyList()
            extractSupportedResolutions()
            field = value
        }

    var isAnalyzing = false // TODO: account for concurrent modification
        private set
    var supportedResolutions: List<Size> = emptyList()

    init {
        extractSupportedResolutions()
    }

    private fun extractScreenRotation(): Int {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display
        } else {
            (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        if (display == null) {
            Log.w(TAG, "Cannot get current display, assuming rotation is $DEFAULT_SCREEN_ROTATION.")
            return DEFAULT_SCREEN_ROTATION
        }
        return when (display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> { // Should not happen
                Log.wtf(TAG, "Unknown display rotation, assuming $DEFAULT_SCREEN_ROTATION.")
                DEFAULT_SCREEN_ROTATION
            }
        }
    }

    private fun extractSupportedResolutions() {
        ProcessCameraProvider.getInstance(activity).addListener({
            Log.d(TAG, "Retrieving supported resolutions.")

            val cameraProvider = cameraProviderFuture.get()

            val camera = try {
                cameraProvider.bindToLifecycle(activity, cameraSelector)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Camera binding failed", e)
                return@addListener
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Camera binding failed", e)
                return@addListener
            }

            supportedResolutions = camera.getSupportedResolutions()
        }, ContextCompat.getMainExecutor(activity))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun Camera.getSupportedResolutions(): List<Size> {
        // Get camera configs
        val cameraId = Camera2CameraInfo.from(cameraInfo).cameraId
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            ?: run { // Should not happen
                Log.wtf(TAG, "Failed to retrieve configs of $this.")
                return emptyList()
            }

        // Check format is supported
        if (!configs.isOutputSupportedFor(IMAGE_FORMAT_CAMERA2)) {
            Log.e(
                TAG,
                "Selected format $IMAGE_FORMAT_CAMERA2 is unsupported. " +
                    "Supported formats: ${configs.outputFormats.joinToString(", ")}."
            )
            return emptyList()
        }

        // Get resolutions
        val resolutions = configs.getOutputSizes(IMAGE_FORMAT_CAMERA2)

        // Rotate resolutions if needed
        Log.d(
            TAG,
            "Current rotations: " +
                "screen -- $screenRotation, " +
                "sensor -- ${cameraInfo.sensorRotationDegrees}."
        )
        if (abs(screenRotation - cameraInfo.sensorRotationDegrees) % ROTATION_SAVING_STEP != 0) {
            Log.i(TAG, "Rotating resolutions.")
            resolutions.forEachIndexed { i, size -> resolutions[i] = size.flipped() }
        }

        return resolutions.sortedBy { it.width }.apply {
            Log.i(TAG, "Supported resolutions: ${joinToString(", ")}.")
        }
    }

    private fun Size.flipped() = Size(height, width)

    fun startImageAnalysis(targetResolution: Size?, callback: (realResolution: Size?) -> Unit) {
        Log.i(TAG, "Starting image analysis targeting $targetResolution.")

        ProcessCameraProvider.getInstance(activity).addListener({
            val cameraProvider = cameraProviderFuture.get()

            val imageAnalysis = ImageAnalysis.Builder()
                .setOutputImageFormat(IMAGE_FORMAT_CAMERAX)
                .setOutputImageRotationEnabled(true)
                .apply { targetResolution?.let { setTargetResolution(it) } }
                .build()
                .apply { setAnalyzer(cameraExecutor, analyzer) }

            cameraProvider.unbindAll() // Currently only binding image analysis
            isAnalyzing = false

            try {
                cameraProvider.bindToLifecycle(activity, cameraSelector, imageAnalysis)
                isAnalyzing = true
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Camera binding failed", e)
                callback(null)
                return@addListener
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Camera binding failed", e)
                callback(null)
                return@addListener
            }

            val realResolution = imageAnalysis.resolutionInfo?.run {
                // No need to account for screenRotation here
                if (rotationDegrees % ROTATION_SAVING_STEP == 0) resolution else resolution.flipped()
            }
            if (realResolution != targetResolution) {
                Log.e(TAG, "Targeted $targetResolution, but got $realResolution instead.")
            }

            callback(realResolution)

            Log.i(TAG, "Started image analysis on $realResolution.")
        }, ContextCompat.getMainExecutor(activity))
    }

    fun stopImageAnalysis() {
        if (!isAnalyzing) {
            Log.i(TAG, "Image analysis already stopped.")
            return
        }

        Log.i(TAG, "Stopping image analysis.")

        ProcessCameraProvider.getInstance(activity).addListener({
            cameraProviderFuture.get().unbindAll()  // Currently only binding image analysis
            Log.i(TAG, "Stopped image analysis.")
        }, ContextCompat.getMainExecutor(activity))
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
