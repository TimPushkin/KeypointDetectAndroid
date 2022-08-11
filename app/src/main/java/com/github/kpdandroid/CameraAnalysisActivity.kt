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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.core.content.ContextCompat
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.UnitedBottomMenu
import com.github.kpdandroid.ui.screens.CameraAnalysisScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.ui.viewmodels.CameraAnalysisViewModel
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.camera.CameraHandler
import com.github.kpdandroid.utils.camera.SnapshotAnalyzer
import com.github.kpdandroid.utils.detection.KeypointDetectionAlgorithm

private const val TAG = "CameraAnalysisActivity"

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

        viewModel.keypointDetector = KeypointDetectionAlgorithm.constructDetectorFrom(
            algorithmName = preferencesManager.selectedCameraAlgorithmName,
            context = this,
            width = viewModel.keypointDetector?.width ?: 0,
            height = viewModel.keypointDetector?.height ?: 0
        )

        initLayout()
    }

    private fun initLayout() {
        setContent {
            KeypointDetectAppTheme {
                viewModel.setKeypointColor(MaterialTheme.colors.primary)

                CameraAnalysisScreen(
                    imageLayers = viewModel.imageLayers?.toList() ?: emptyList(),
                    calcTimeMs = viewModel.calcTimeMs,
                    isCameraPermissionGranted = isCameraPermissionGranted(),
                    bottomMenu = {
                        UnitedBottomMenu(horizontalArrangement = Arrangement.SpaceEvenly) {
                            ExpandableBottomMenuItem(
                                options = KeypointDetectionAlgorithm.names,
                                selectedOption = preferencesManager.selectedCameraAlgorithmName,
                                onSelected = this@CameraAnalysisActivity::onAlgorithmSelected,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = "Detection algorithm"
                                    )
                                }
                            )

                            ExpandableBottomMenuItem(
                                options = cameraHandler.supportedResolutions.map { it.toString() },
                                selectedOption = preferencesManager.selectedResolution?.toString()
                                    ?: "Pick resolution",
                                onSelected = this@CameraAnalysisActivity::onResolutionSelected,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
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

    private fun onAlgorithmSelected(algorithmName: String) {
        preferencesManager.selectedCameraAlgorithmName = algorithmName
        viewModel.keypointDetector =
            KeypointDetectionAlgorithm.constructDetectorFrom(
                algorithmName = algorithmName,
                context = this@CameraAnalysisActivity,
                width = viewModel.keypointDetector?.width ?: 0,
                height = viewModel.keypointDetector?.height ?: 0
            )
    }

    private fun onResolutionSelected(resolutionString: String) {
        cameraHandler.startImageAnalysis(
            targetResolution = Size.parseSize(resolutionString),
            callback = preferencesManager::selectedResolution::set
        )
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
