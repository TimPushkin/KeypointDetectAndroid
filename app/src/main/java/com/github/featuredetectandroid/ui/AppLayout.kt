package com.github.featuredetectandroid.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.featuredetectandroid.utils.KeypointDetectionAlgorithm

@Composable
fun AppLayout(
    isCameraPermissionGranted: Boolean,
    keypointOffsets: List<Offset>,
    frameBitmap: Bitmap?,
    frames: Int,
    milliseconds: Long,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isCameraPermissionGranted) {
                frameBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Grayscale photo"
                    )

                    val (width, height) = with(LocalDensity.current) {
                        bitmap.width.toDp() to bitmap.height.toDp()
                    }

                    Canvas(modifier = Modifier.size(width, height)) {
                        drawPoints(
                            points = keypointOffsets,
                            pointMode = PointMode.Points,
                            color = Color.Blue,
                            strokeWidth = 10f
                        )
                    }

                    Text(
                        text = "Average keypoints detection time: " +
                            "${if (frames != 0) milliseconds / frames else "-"} ms.",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                vertical = 30.dp,
                                horizontal = 20.dp
                            ),
                        style = MaterialTheme.typography.body1
                    )
                }
            } else {
                Text("Camera permission required")
            }
        }
    }
}
