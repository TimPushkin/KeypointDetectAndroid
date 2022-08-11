package com.github.kpdandroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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

private const val MIN_RUN_TIMES = 1

@Composable
fun FileAnalysisScreen(
    imageLayers: List<ImageBitmap>,
    calcTimesMs: Pair<Double, Double>?,
    detectionProgress: Float?,
    showFab: Boolean,
    onStartClick: (Int) -> Unit,
    onStopClick: () -> Unit,
    bottomMenu: @Composable () -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    // Saved here to keep between dialog reopening
    var dialogEnteredNum by rememberSaveable { mutableStateOf(MIN_RUN_TIMES) }

    if (openDialog) {
        RunConfigurationDialog(
            savedEnteredNum = dialogEnteredNum,
            onDismiss = { openDialog = false },
            onConfirm = { enteredNum ->
                onStartClick(enteredNum)
                openDialog = false
                dialogEnteredNum = enteredNum
            }
        )
    }

    Scaffold(
        bottomBar = bottomMenu,
        floatingActionButton = {
            when {
                !showFab -> Unit
                detectionProgress == null -> StartFab { openDialog = true }
                else -> StopFab(
                    progress = detectionProgress,
                    onClick = onStopClick
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) { paddingValues ->
        DetectionResultColumn(
            imageLayers = imageLayers,
            altText = "Pick an image for analysis",
            captions = calcTimesMs?.let { (calcTimeMs, stdDevMs) ->
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

@Composable
private fun RunConfigurationDialog(
    savedEnteredNum: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var enteredText by rememberSaveable { mutableStateOf(savedEnteredNum.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                val onDone = {
                    val enteredNum = enteredText.toIntOrNull()?.coerceAtLeast(MIN_RUN_TIMES)
                        ?: MIN_RUN_TIMES
                    enteredText = enteredNum.toString()
                    onConfirm(enteredNum)
                }

                OutlinedTextField(
                    value = enteredText,
                    onValueChange = { enteredText = it },
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

@Composable
private fun StopFab(progress: Float, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        CircularProgressIndicator(progress = progress)

        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop"
        )
    }
}
