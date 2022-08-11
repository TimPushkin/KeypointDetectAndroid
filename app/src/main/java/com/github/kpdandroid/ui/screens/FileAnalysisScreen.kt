package com.github.kpdandroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.kpdandroid.ui.DetectionResultColumn

@Composable
fun FileAnalysisScreen(
    imageLayers: List<ImageBitmap>,
    calcTimeMs: Pair<Double, Double>?,
    showStartButton: Boolean,
    onStartClick: (Int) -> Unit,
    bottomMenu: @Composable () -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }

    if (openDialog) {
        RunConfigurationDialog(
            onDismiss = { openDialog = false },
            onConfirm = { enteredNum ->
                onStartClick(enteredNum)
                openDialog = false
            }
        )
    }

    Scaffold(
        bottomBar = bottomMenu,
        floatingActionButton = { if (showStartButton) StartFab { openDialog = true } },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) { paddingValues ->
        DetectionResultColumn(
            imageLayers = imageLayers,
            altText = "Pick an image for analysis",
            captions = calcTimeMs?.let { (calcTimeMs, stdDevMs) ->
                listOf(
                    "Mean detection time: ${formatTime(calcTimeMs)} ms.",
                    "Standard deviation: ${formatTime(stdDevMs)} ms."
                )
            } ?: listOf("Analyze an image to see detection time"),
            modifier = Modifier.padding(paddingValues)
        )
    }
}

private fun formatTime(time: Double) = "%5f".format(time)

private const val MIN_RUN_TIMES = 1

@Composable
private fun RunConfigurationDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var alertDialogText by rememberSaveable { mutableStateOf(MIN_RUN_TIMES.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                val onDone = {
                    val enteredNum = alertDialogText.toIntOrNull()?.coerceAtLeast(MIN_RUN_TIMES)
                        ?: MIN_RUN_TIMES
                    alertDialogText = enteredNum.toString()
                    onConfirm(enteredNum)
                }

                OutlinedTextField(
                    value = alertDialogText,
                    onValueChange = { alertDialogText = it },
                    label = { Text("Number of runs") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onAny = { onDone() })
                )

                TextButton(onClick = onDone) { Text("START") }
            }
        }
    )
}

@Composable
private fun StartFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text("START") },
        icon = {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start"
            )
        },
        onClick = onClick
    )
}
