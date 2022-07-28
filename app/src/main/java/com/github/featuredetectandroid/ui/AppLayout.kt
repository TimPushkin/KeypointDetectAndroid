package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.featuredetectandroid.utils.KeypointDetectionAlgorithm

private const val KEYPOINT_WIDTH = 10f

@Composable
fun AppLayout(
    isCameraPermissionGranted: Boolean,
    keypointOffsets: List<Offset>,
    frameBitmap: Bitmap?,
    calcTimeMs: Long,
    selectedAlgorithm: String,
    onAlgorithmSelected: (String) -> Unit
) {
    Scaffold(
        drawerContent = {
            Menu(
                header = "Keypoint detection algorithm:",
                options = KeypointDetectionAlgorithm.names,
                selectedOption = selectedAlgorithm,
                onSelected = onAlgorithmSelected
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isCameraPermissionGranted) {
                Text("Camera permission required")
                return@Column
            }

            val keypointPaint = Paint().apply {
                color = Color.Blue
                strokeWidth = KEYPOINT_WIDTH
            }

            frameBitmap?.asImageBitmap()?.let { bitmap ->
                Canvas(bitmap).drawPoints(
                    pointMode = PointMode.Points,
                    points = keypointOffsets,
                    paint = keypointPaint
                )

                Image(
                    bitmap = bitmap,
                    contentDescription = "Grayscale photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )

                if (keypointOffsets.isNotEmpty() ||
                    selectedAlgorithm != KeypointDetectionAlgorithm.NONE.algorithmName
                ) {
                    Text(
                        text = "Detection time: $calcTimeMs ms.",
                        modifier = Modifier.padding(
                            vertical = 30.dp,
                            horizontal = 20.dp
                        )
                    )
                }
            }
        }
    }
}
