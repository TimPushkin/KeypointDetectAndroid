package com.github.kpdandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.core.content.ContextCompat
import com.github.kpdandroid.ui.BottomMenu
import com.github.kpdandroid.ui.BottomMenuItem
import com.github.kpdandroid.ui.screens.CameraAnalysisScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.ui.viewmodels.CameraAnalysisViewModel
import com.github.kpdandroid.utils.KeypointDetectionAlgorithm
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.camera.CameraHandler
import com.github.kpdandroid.utils.camera.SnapshotAnalyzer

private const val TAG = "CameraActivity"

class CameraAnalysisActivity : ComponentActivity() {
    private val viewModel by viewModels<CameraAnalysisViewModel>()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var cameraHandler: CameraHandler

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Log.i(TAG, "Camera permission was granted.")
                startCameraAnalysisIfNeeded()
            } else {
                Log.e(TAG, "Camera permission was NOT granted.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = (application as KeypointDetectApp).preferencesManager
        cameraHandler = CameraHandler(this, SnapshotAnalyzer(viewModel))

        viewModel.keypointDetector = KeypointDetectionAlgorithm.constructKeypointDetector(
            algorithmName = preferencesManager.selectedAlgorithmName,
            context = this,
            width = viewModel.keypointDetector?.width ?: 0,
            height = viewModel.keypointDetector?.height ?: 0
        )

        initLayout()
    }

    private fun initLayout() {
        setContent {
            KeypointDetectAppTheme {
                viewModel.painter.pointColor = MaterialTheme.colors.primary

                CameraAnalysisScreen(
                    image = viewModel.paintedSnapshot,
                    calcTimeMs = viewModel.calcTimeMs,
                    isCameraPermissionGranted = isCameraPermissionGranted(),
                    bottomMenu = {
                        BottomMenu {
                            BottomMenuItem(
                                options = KeypointDetectionAlgorithm.names,
                                selectedOption = preferencesManager.selectedAlgorithmName,
                                onSelected = { algorithmName ->
                                    preferencesManager.selectedAlgorithmName = algorithmName
                                    viewModel.keypointDetector =
                                        KeypointDetectionAlgorithm.constructKeypointDetector(
                                            algorithmName = algorithmName,
                                            context = this@CameraAnalysisActivity,
                                            width = viewModel.keypointDetector?.width ?: 0,
                                            height = viewModel.keypointDetector?.height ?: 0
                                        )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = "Detection algorithm"
                                    )
                                }
                            )

                            BottomMenuItem(
                                options = cameraHandler.supportedResolutions.map { it.toString() },
                                selectedOption = preferencesManager.selectedResolution?.toString()
                                    ?: "Pick resolution",
                                onSelected = { sizeString ->
                                    cameraHandler.startImageAnalysis(
                                        targetResolution = Size.parseSize(sizeString),
                                        callback = preferencesManager::selectedResolution::set
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccountBox,
                                        contentDescription = "Camera resolution"
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    // Not using onResume to account for changes made in split screen
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        if (isTopResumedActivity) startCameraAnalysisIfNeeded()
    }

    private fun startCameraAnalysisIfNeeded() {
        if (cameraHandler.isAnalyzing) return

        when {
            isCameraPermissionGranted() ->
                cameraHandler.startImageAnalysis(
                    targetResolution = preferencesManager.selectedResolution,
                    callback = preferencesManager::selectedResolution::set
                )
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

    override fun onDestroy() {
        cameraHandler.shutdown()
        super.onDestroy()
    }
}
