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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kpdandroid.ui.screens.CameraAnalysisScreen
import com.github.kpdandroid.ui.screens.FileAnalysisScreen
import com.github.kpdandroid.ui.screens.MAIN_SCREEN
import com.github.kpdandroid.ui.screens.NavDestination
import com.github.kpdandroid.ui.screens.NavScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.ui.viewmodels.CameraAnalysisViewModel
import com.github.kpdandroid.ui.viewmodels.FileAnalysisViewModel
import com.github.kpdandroid.ui.viewmodels.ImageAnalysisViewModel
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.camera.CameraHandler
import com.github.kpdandroid.utils.detection.DetectionAlgo

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var prefs: PreferencesManager
    private lateinit var cameraHandler: CameraHandler

    // ViewModels must be accessed only after prefs is initialized
    private val fileViewModel by viewModels<FileAnalysisViewModel>(
        factoryProducer = this::createViewModel
    )
    private val cameraViewModel by viewModels<CameraAnalysisViewModel>(
        factoryProducer = this::createViewModel
    )

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Log.i(TAG, "Camera permission was granted.")
                // In Android Q and later permission window triggers onTopResumedActivityChanged()
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    tryStartCameraAnalysisIfNeeded()
                }
            } else {
                Log.e(TAG, "Camera permission was NOT granted.")
            }
        }

    private fun createViewModel(): ViewModelProvider.Factory {
        check(this::prefs.isInitialized) {
            "ImageAnalysisViewModel.Factory accessed before prefs initialization"
        }
        return ImageAnalysisViewModel.Factory(prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = (application as KeypointDetectApp).prefs
        cameraHandler = CameraHandler(this, cameraViewModel.analyzer)

        // In case starting for the first time
        fileViewModel ensureUsesDetectorTitled prefs.fileAlgoTitle
        cameraViewModel ensureUsesDetectorTitled prefs.cameraAlgoTitle

        setContent {
            KeypointDetectAppTheme {
                val navController = rememberNavController()

                fileViewModel.keypointColor = MaterialTheme.colors.primary
                cameraViewModel.keypointColor = MaterialTheme.colors.primary

                NavHost(navController = navController, startDestination = MAIN_SCREEN) {
                    composable(MAIN_SCREEN) { NavScreen { navController.navigate(it.name) } }
                    composable(NavDestination.FILE_ANALYSIS.name) {
                        FileAnalysisScreen(fileViewModel)
                    }
                    composable(NavDestination.CAMERA_ANALYSIS.name) { backStackEntry ->
                        LaunchedEffect(backStackEntry) {
                            tieCameraLifecycleTo(backStackEntry)
                            tryStartCameraAnalysisIfNeeded()
                        }

                        CameraAnalysisScreen(
                            vm = cameraViewModel,
                            supportedResolutions =
                            cameraHandler.supportedResolutions.map { it.toString() },
                            onResolutionSelected = this@MainActivity::tryStartCameraAnalysis
                        )
                    }
                }
            }
        }
    }

    private infix fun ImageAnalysisViewModel.ensureUsesDetectorTitled(algoTitle: String) {
        if (DetectionAlgo.from(keypointDetector)?.title != algoTitle) {
            keypointDetector = DetectionAlgo.constructDetectorFrom(
                algoTitle = algoTitle,
                context = this@MainActivity,
                width = keypointDetector?.width ?: 0,
                height = keypointDetector?.height ?: 0
            )
        }
    }

    private fun tieCameraLifecycleTo(owner: LifecycleOwner) {
        Log.i(TAG, "Tying camera lifecycle to $owner.")

        if (cameraHandler.isAnalyzing()) {
            Log.d(TAG, "Stopping image analysis first.")
            cameraHandler.stopImageAnalysis()
        }

        cameraHandler.tieCameraLifecycleIfNeededTo(
            owner.apply {
                lifecycle.addObserver(
                    object : LifecycleEventObserver {
                        override fun onStateChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                Log.i(TAG, "Untying camera lifecycle from $owner.")
                                cameraHandler.untieCameraLifecycleIfNeededFrom(owner)
                            }
                        }
                    }
                )
            }
        )
    }

    private fun tryStartCameraAnalysisIfNeeded() {
        if (cameraHandler.run { isCameraLifecycleTied && !isAnalyzing() }) tryStartCameraAnalysis()
    }

    private fun tryStartCameraAnalysis() {
        val isCameraPermissionGranted = isPermissionGranted(Manifest.permission.CAMERA)
        cameraViewModel.isCameraPermissionGranted = isCameraPermissionGranted

        Log.i(
            TAG,
            "Trying to start camera analysis (permissions granted: $isCameraPermissionGranted)."
        )

        when {
            isCameraPermissionGranted -> cameraHandler.startImageAnalysis(
                targetResolution = prefs.resolution,
                callback = prefs::resolution::set
            )
            shouldRationalize(Manifest.permission.CAMERA) -> Toast.makeText(
                this,
                "Camera permission is required for camera analysis.",
                Toast.LENGTH_SHORT
            ).show()
            else -> cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun shouldRationalize(permission: String) =
        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) &&
            shouldShowRequestPermissionRationale(permission)

    // Not using onResume to account for changes made in split screen
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        if (isTopResumedActivity) tryStartCameraAnalysisIfNeeded()
    }

    override fun onDestroy() {
        cameraHandler.shutdown()
        super.onDestroy()
    }
}
