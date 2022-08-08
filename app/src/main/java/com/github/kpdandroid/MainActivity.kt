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
import com.github.kpdandroid.ui.AppLayout
import com.github.kpdandroid.ui.BottomMenu
import com.github.kpdandroid.ui.BottomMenuItem
import com.github.kpdandroid.ui.SnapshotViewModel
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.utils.CameraHandler
import com.github.kpdandroid.utils.KeypointDetectionAlgorithm
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.SnapshotAnalyzer

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val snapshotViewModel by viewModels<SnapshotViewModel>()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var cameraHandler: CameraHandler

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was granted successfully.")
                startCameraAnalysisIfNeeded()
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission was NOT granted.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = (application as KeypointDetectApp).preferencesManager
        cameraHandler = CameraHandler(this, SnapshotAnalyzer(snapshotViewModel))

        snapshotViewModel.keypointDetector = KeypointDetectionAlgorithm.constructKeypointDetector(
            algorithmName = preferencesManager.selectedAlgorithmName,
            context = this,
            width = snapshotViewModel.keypointDetector?.width ?: 0,
            height = snapshotViewModel.keypointDetector?.height ?: 0
        )

        initLayout()
    }

    private fun initLayout() {
        setContent {
            KeypointDetectAppTheme {
                snapshotViewModel.painter.pointColor = MaterialTheme.colors.primary

                AppLayout(
                    image = snapshotViewModel.paintedSnapshot,
                    calcTimeMs = snapshotViewModel.calcTimeMs,
                    isCameraPermissionGranted = isCameraPermissionGranted(),
                    bottomMenu = {
                        BottomMenu {
                            BottomMenuItem(
                                options = KeypointDetectionAlgorithm.names,
                                selectedOption = preferencesManager.selectedAlgorithmName,
                                onSelected = { algorithmName ->
                                    preferencesManager.selectedAlgorithmName = algorithmName
                                    snapshotViewModel.keypointDetector =
                                        KeypointDetectionAlgorithm.constructKeypointDetector(
                                            algorithmName = algorithmName,
                                            context = this@MainActivity,
                                            width = snapshotViewModel.keypointDetector?.width ?: 0,
                                            height = snapshotViewModel.keypointDetector?.height ?: 0
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
