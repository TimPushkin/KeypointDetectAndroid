package com.github.kpdandroid.ui

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
import com.github.kpdandroid.utils.KeypointDetectionAlgorithm

@Composable
fun AppLayout(
    image: ImageBitmap?,
    calcTimeMs: Long?,
    isCameraPermissionGranted: Boolean,
    selectedAlgorithmName: String,
    onAlgorithmSelected: (String) -> Unit
) {
    Scaffold(
        drawerContent = {
            Menu(
                header = "Keypoint detection algorithm:",
                options = KeypointDetectionAlgorithm.names,
                selectedOption = selectedAlgorithmName,
                onSelected = onAlgorithmSelected
            )
        },
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
            image?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Grayscale photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )

                val detectionTime = calcTimeMs?.let { "$it ms" } ?: "unknown"

                Text(
                    text = "Detection time: $detectionTime",
                    modifier = Modifier.padding(30.dp)
                )
            }
        }
    }
}
