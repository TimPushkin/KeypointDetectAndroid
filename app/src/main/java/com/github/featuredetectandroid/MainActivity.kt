package com.github.featuredetectandroid

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
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.github.featuredetectandroid.ui.AppLayout
import com.github.featuredetectandroid.ui.OutputViewModel
import com.github.featuredetectandroid.ui.theme.FeatureDetectAppTheme
import com.github.featuredetectandroid.utils.PhotoAnalyzer
import com.github.featuredetectandroid.utils.PreferencesManager
import com.github.featuredetectandroid.utils.selectFeatureDetector
import java.util.concurrent.Executors

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val outputViewModel by viewModels<OutputViewModel>()
    private lateinit var preferencesManager: PreferencesManager

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was granted successfully.")
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was NOT granted.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputViewModel.isCameraPermissionGranted = isCameraPermissionGranted()
        tryStartCamera()
        preferencesManager = PreferencesManager(this)
        outputViewModel.featureDetector = selectFeatureDetector(
            this@MainActivity,
            preferencesManager.getSelectedAlgorithm(),
            outputViewModel.getSize().first,
            outputViewModel.getSize().second
        )

        setContent {
            FeatureDetectAppTheme {
                var selectedAlgorithm by remember {
                    mutableStateOf(preferencesManager.getSelectedAlgorithm())
                }
                AppLayout(
                    context = this,
                    outputViewModel = outputViewModel,
                    preferencesManager = preferencesManager,
                    selectedAlgorithm = selectedAlgorithm,
                    onAlgorithmSelected = { algorithmName -> selectedAlgorithm = algorithmName }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        outputViewModel.isCameraPermissionGranted = isCameraPermissionGranted()
        tryStartCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun tryStartCamera() = when {
        outputViewModel.isCameraPermissionGranted -> startCamera()
        shouldRationalize() -> {
            Toast.makeText(
                this,
                "Without camera permission app can't get and display keypoints.",
                Toast.LENGTH_SHORT
            ).show()
        }
        else -> cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun shouldRationalize() =
        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) &&
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        PhotoAnalyzer(outputViewModel)
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
            } catch (illegalState: IllegalStateException) {
                Log.e(TAG, "Use case binding failed: ", illegalState)
            } catch (illegalArgument: IllegalArgumentException) {
                Log.e(TAG, "Use case binding failed: ", illegalArgument)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
