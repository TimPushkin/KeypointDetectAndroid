package com.github.kpdandroid.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import com.github.kpdandroid.KeypointDetectApp
import com.github.kpdandroid.utils.camera.SnapshotAnalyzer

class CameraAnalysisViewModel(app: Application) :
    ImageAnalysisViewModel(app, { (app as KeypointDetectApp).prefs.cameraAlgoTitle }) {
    val analyzer = SnapshotAnalyzer(
        detectorGetter = { keypointDetector },
        onResult = { snapshot, keypoints, calcTimeMs ->
            updateMainLayer(snapshot.asImageBitmap())
            drawKeypoints(keypoints)
            this.calcTimeMs = calcTimeMs
        }
    )

    var isCameraPermissionGranted by mutableStateOf(false)

    var calcTimeMs: Long? by mutableStateOf(null)
}
