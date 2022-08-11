package com.github.kpdandroid.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.github.kpdandroid.ui.DetectionResultColumn

@Composable
fun CameraAnalysisScreen(
    imageLayers: List<ImageBitmap>,
    calcTimeMs: Long?,
    isCameraPermissionGranted: Boolean,
    bottomMenu: @Composable () -> Unit
) {
    Scaffold(bottomBar = bottomMenu) { paddingValues ->
        if (!isCameraPermissionGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera permission required")
            }
            return@Scaffold // Compose crashes when using return inside the Column below on release
        }

        DetectionResultColumn(
            imageLayers = imageLayers,
            altText = "Snapshot cannot be displayed",
            captions = listOf(
                calcTimeMs?.let { "Latest detection time: $it ms" }
                    ?: "Pick an algorithm to see detection time"
            ),
            modifier = Modifier.padding(paddingValues)
        )
    }
}
