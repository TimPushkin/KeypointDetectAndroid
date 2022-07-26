package com.github.featuredetectandroid.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import com.github.featuredetectandroid.utils.KeypointDetectionAlgorithm
import com.github.featuredetectandroid.utils.PreferencesManager

@Composable
fun AppLayout(
    context: Context,
    outputViewModel: OutputViewModel,
    preferencesManager: PreferencesManager,
    selectedAlgorithm: String,
    onAlgorithmSelected: (String) -> Unit
) = Scaffold(
    drawerContent = {
        Menu(
            header = "Keypoint detection algorithm:",
            options = KeypointDetectionAlgorithm.names,
            selectedOption = selectedAlgorithm,
            onSelected = { algorithmName ->
                preferencesManager.putSelectedAlgorithm(algorithmName)
                onAlgorithmSelected(algorithmName)
                outputViewModel.keypointOffsets = emptyList()
                outputViewModel.featureDetector = KeypointDetectionAlgorithm.nameToFeatureDetector(
                    context = context,
                    algorithmName = preferencesManager.getSelectedAlgorithm(),
                    width = outputViewModel.featureDetector?.width ?: 0,
                    height = outputViewModel.featureDetector?.height ?: 0
                )
            }
        )
    },
    modifier = Modifier.fillMaxSize()
) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        if (outputViewModel.isCameraPermissionGranted) {
            outputViewModel.grayscaleBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Grayscale photo"
                )

                val (width, height) = with(LocalDensity.current) {
                    bitmap.width.toDp() to bitmap.height.toDp()
                }

                Canvas(modifier = Modifier.size(width, height)) {
                    drawPoints(
                        points = outputViewModel.keypointOffsets,
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
