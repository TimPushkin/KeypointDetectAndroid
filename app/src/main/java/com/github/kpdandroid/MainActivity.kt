package com.github.kpdandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material.MaterialTheme
import androidx.core.content.ContextCompat
import com.github.kpdandroid.ui.AppLayout
import com.github.kpdandroid.ui.SnapshotViewModel
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.utils.KeypointDetectionAlgorithm
import com.github.kpdandroid.utils.PhotoAnalyzer
import com.github.kpdandroid.utils.PreferencesManager
import java.util.concurrent.Executors

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val snapshotViewModel by viewModels<SnapshotViewModel>()
    private lateinit var preferencesManager: PreferencesManager
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var isAnalyzing = false

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was granted successfully.")
                startCameraAnalysis()
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was NOT granted.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = (application as KeypointDetectApp).preferencesManager

        snapshotViewModel.keypointDetector = KeypointDetectionAlgorithm.constructKeypointDetector(
            algorithmName = preferencesManager.selectedAlgorithmName,
            context = this,
            width = snapshotViewModel.keypointDetector?.width ?: 0,
            height = snapshotViewModel.keypointDetector?.height ?: 0
        )

        setContent {
            KeypointDetectAppTheme {
                snapshotViewModel.painter.pointColor = MaterialTheme.colors.primary

                AppLayout(
                    image = snapshotViewModel.paintedSnapshot,
                    calcTimeMs = snapshotViewModel.calcTimeMs,
                    isCameraPermissionGranted = isCameraPermissionGranted(),
                    initialAlgorithmName = preferencesManager.selectedAlgorithmName,
                    onAlgorithmSelected = { algorithmName ->
                        preferencesManager.selectedAlgorithmName = algorithmName
                        snapshotViewModel.keypointDetector =
                            KeypointDetectionAlgorithm.constructKeypointDetector(
                                algorithmName = algorithmName,
                                context = this,
                                width = snapshotViewModel.keypointDetector?.width ?: 0,
                                height = snapshotViewModel.keypointDetector?.height ?: 0
                            )
                    }
                )
            }
        }
    }

    // Not using onResume to account for changes made in split screen
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        if (isTopResumedActivity) startCameraAnalysisIfNeeded()
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    private fun startCameraAnalysisIfNeeded() {
        if (isAnalyzing) return

        when {
            isCameraPermissionGranted() -> startCameraAnalysis()
            shouldRationalize() -> {
                Toast.makeText(
                    this,
                    "Without camera permission app can't get and display keypoints.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun shouldRationalize() =
        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) &&
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)

    private fun startCameraAnalysis() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setOutputImageRotationEnabled(true)
                .build()
                .apply { setAnalyzer(cameraExecutor, PhotoAnalyzer(snapshotViewModel)) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll() // Just in case

            try {
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
                isAnalyzing = true
            } catch (illegalState: IllegalStateException) {
                Log.e(TAG, "Use case binding failed: ", illegalState)
            } catch (illegalArgument: IllegalArgumentException) {
                Log.e(TAG, "Use case binding failed: ", illegalArgument)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
