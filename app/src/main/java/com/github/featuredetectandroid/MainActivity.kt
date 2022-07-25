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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import com.github.featuredetectandroid.ui.Menu
import com.github.featuredetectandroid.ui.OutputViewModel
import com.github.featuredetectandroid.ui.theme.FeatureDetectAppTheme
import com.github.featuredetectandroid.utils.KeypointDetectionAlgorithm
import com.github.featuredetectandroid.utils.PhotoAnalyzer
import com.github.featuredetectandroid.utils.PreferencesManager
import com.github.featuredetectandroid.utils.selectFeatureDetector
import java.util.concurrent.Executors

private const val TAG = "MainActivity"

private const val RESOLUTION_WIDTH = 640
private const val RESOLUTION_HEIGHT = 480

private const val DEFAULT_ALGORITHM = "None"

class MainActivity : ComponentActivity() {
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val imageViewModel by viewModels<OutputViewModel>()
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

        imageViewModel.isCameraPermissionGranted = isCameraPermissionGranted()
        tryStartCamera()
        preferencesManager = PreferencesManager(this)
        preferencesManager.putSelectedAlgorithm(DEFAULT_ALGORITHM)

        setContent {
            FeatureDetectAppTheme {
                var selectedAlgorithm by remember {
                    mutableStateOf(preferencesManager.getSelectedAlgorithm())
                }
                AppLayout(
                    selectedAlgorithm = selectedAlgorithm,
                    onAlgorithmSelected = { algorithmName -> selectedAlgorithm = algorithmName }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        imageViewModel.isCameraPermissionGranted = isCameraPermissionGranted()
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
                // .setTargetResolution(Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                // For some reason image is always 720 x 720 if this line is active.
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        PhotoAnalyzer(imageViewModel)
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

    @Composable
    fun AppLayout(selectedAlgorithm: String, onAlgorithmSelected: (String) -> Unit) = Scaffold(
        drawerContent = {
            Menu(
                header = "Keypoint detection algorithm:",
                options = KeypointDetectionAlgorithm.names,
                selectedOption = selectedAlgorithm,
                onSelected = { algorithmName ->
                    preferencesManager.putSelectedAlgorithm(algorithmName)
                    onAlgorithmSelected(algorithmName)
                    imageViewModel.setKeypointsForOutput(emptyList())
                    imageViewModel.featureDetector = selectFeatureDetector(
                        this@MainActivity,
                        algorithmName,
                        imageViewModel.getSize().first,
                        imageViewModel.getSize().second
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageViewModel.isCameraPermissionGranted) {
                imageViewModel.grayscaleBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Grayscale photo"
                    )

                    val (width, height) = with(LocalDensity.current) {
                        bitmap.width.toDp() to bitmap.height.toDp()
                    }

                    Canvas(modifier = Modifier.size(width, height)) {
                        drawPoints(
                            points = imageViewModel.keypointsOffset,
                            pointMode = PointMode.Points,
                            color = Color.Blue,
                            strokeWidth = 10f
                        )
                    }
                }
            } else {
                Text("Camera permission required")
            }
        }
    }
}
