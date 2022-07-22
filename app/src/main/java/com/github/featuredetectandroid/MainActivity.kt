package com.github.featuredetectandroid

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import com.github.featuredetectandroid.ui.GrayscaleViewModel
import com.github.featuredetectandroid.ui.Menu
import com.github.featuredetectandroid.ui.theme.FeatureDetectAppTheme
import com.github.featuredetectandroid.utils.PhotoAnalyzer
import java.util.concurrent.Executors

private const val TAG = "MainActivity"

private const val RESOLUTION_WIDTH = 640
private const val RESOLUTION_HEIGHT = 360

class MainActivity : ComponentActivity() {
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val imageViewModel by viewModels<GrayscaleViewModel>()
    private lateinit var currentAlgorithm: SharedPreferences
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

        currentAlgorithm = this.getPreferences(Context.MODE_PRIVATE)

        imageViewModel.isCameraPermissionGranted = cameraPermissionGranted()
        tryStartCamera()

        setContent {
            val scaffoldState = rememberScaffoldState()
            FeatureDetectAppTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = { Menu(currentAlgorithm) }
                ) {
                    Box(Modifier.fillMaxSize()) {
                        if (imageViewModel.isCameraPermissionGranted) {
                            imageViewModel.grayscaleBitmap?.let { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = "Grayscale photo"
                                )
                            }
                        } else {
                            Text(
                                text = "Camera permission required",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        imageViewModel.isCameraPermissionGranted = cameraPermissionGranted()
        tryStartCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun tryStartCamera() = when {
        imageViewModel.isCameraPermissionGranted -> startCamera()
        shouldRationalize() -> {
            Toast.makeText(
                this,
                "Without camera permission app can't get and display keypoints.",
                Toast.LENGTH_SHORT
            ).show()
        }
        else -> cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun cameraPermissionGranted() = ContextCompat.checkSelfPermission(
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
                .setTargetResolution(Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                .build()
                .apply { setAnalyzer(cameraExecutor, PhotoAnalyzer(imageViewModel)) }

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
