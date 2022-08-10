package com.github.kpdandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun DetectionResultColumn(
    imageLayers: List<ImageBitmap>,
    altText: String,
    captions: List<String>,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageLayers.isNotEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                for (layer in imageLayers) {
                    Image(
                        bitmap = layer,
                        contentDescription = "Image layer",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Text(altText)
        }

        if (captions.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                captions.forEach { Text(it) }
            }
        }
    }
}
