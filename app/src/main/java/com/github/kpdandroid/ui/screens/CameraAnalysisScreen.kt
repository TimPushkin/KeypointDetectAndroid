package com.github.kpdandroid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun CameraAnalysisScreen(
    image: ImageBitmap?,
    calcTimeMs: Long?,
    isCameraPermissionGranted: Boolean,
    bottomMenu: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = bottomMenu,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Camera snapshot",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            } else {
                Text("Snapshot not available")
            }

            val detectionText = calcTimeMs?.let { "Latest detection time: $it ms" }
                ?: "Pick an algorithm to see detection time"

            Text(
                text = detectionText,
                modifier = Modifier.padding(30.dp)
            )
        }
    }
}
