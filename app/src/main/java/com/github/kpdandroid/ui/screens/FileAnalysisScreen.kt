package com.github.kpdandroid.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.kpdandroid.ui.DetectionResultColumn
import com.github.kpdandroid.ui.DualBottomMenu
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.ExpandableBottomMenuItemContent
import com.github.kpdandroid.ui.viewmodels.FileAnalysisViewModel
import com.github.kpdandroid.utils.detection.DetectionAlgo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
// - Not using `vm = viewModel()` as we need to use exactly the same instance as the MainActivity
// does.
// - Not using `vm = viewModel(LocalContext.current as ViewModelStoreOwner)` as it may cause
// unexpected cast exceptions.
fun FileAnalysisScreen(vm: FileAnalysisViewModel) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    RunConfigurationDialog(
        open = openDialog,
        onDismiss = { openDialog = false },
        onConfirm = { enteredNum ->
            vm.startDetection(enteredNum)
            openDialog = false
        }
    )

    Scaffold(
        bottomBar = {
            val context = LocalContext.current
            FileAnalysisMenu(
                algorithmsItem = ExpandableBottomMenuItemContent(
                    options = DetectionAlgo.titles,
                    selectedOption = vm.prefs.fileAlgoTitle,
                    onSelected = { algoTitle -> vm.prefs.fileAlgoTitle = algoTitle }
                ),
                onImagePicked = { vm.setupImage(context.contentResolver.openInputStream(it)) },
                onLogPicked = { vm.setupLogger(context.contentResolver.openOutputStream(it)) }
            )
        },
        floatingActionButton = {
            val scope = rememberCoroutineScope { Dispatchers.Default }
            FileAnalysisFab(
                show = vm.imageLayers != null && vm.prefs.fileAlgoTitle != DetectionAlgo.NONE.title,
                progress = vm.detectionProgress,
                onStart = { openDialog = true },
                onStop = { scope.launch { vm.stopDetection() } }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) { paddingValues ->
        DetectionResultColumn(
            imageLayers = vm.imageLayers?.toList() ?: emptyList(),
            altText = "Pick an image for analysis",
            captions = vm.calcTimesMs?.let { (calcTimeMs, stdDevMs) ->
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
private fun RunConfigurationDialog(open: Boolean, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var enteredText by rememberSaveable { mutableStateOf(MIN_RUN_TIMES.toString()) }

    if (open) {
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
}

private const val IMAGE_MIME = "image/*"
private const val TEXT_MIME = "text/*"
private const val SUGGESTED_LOG_FILENAME = "log.txt"

@Composable
private fun FileAnalysisMenu(
    algorithmsItem: ExpandableBottomMenuItemContent,
    onImagePicked: (Uri) -> Unit,
    onLogPicked: (Uri) -> Unit
) {
    DualBottomMenu(
        horizontalArrangement = Arrangement.Center,
        spacing = 70.dp,
        startItems = {
            ExpandableBottomMenuItem(
                content = algorithmsItem,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Detection algorithm"
                    )
                }
            )
        },
        endItems = {
            val imagePicker = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { it?.let { uri -> onImagePicked(uri) } }
            val logPicker = rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument(TEXT_MIME)
            ) { it?.let { uri -> onLogPicked(uri) } }

            ExpandableBottomMenuItem(
                title = "Files…",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "File picking"
                    )
                },
                optionsWithAction = listOf(
                    "Pick image" to { imagePicker.launch(arrayOf(IMAGE_MIME)) },
                    "Pick log" to { logPicker.launch(SUGGESTED_LOG_FILENAME) }
                ),
            )
        }
    )
}

@Composable
private fun FileAnalysisFab(
    show: Boolean,
    progress: Float?,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    when {
        !show -> Unit
        progress == null -> StartFab(onClick = onStart)
        else -> StopFab(progress = progress, onClick = onStop)
    }
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
        if (progress > 0) {
            CircularProgressIndicator(progress = progress)
        } else {
            CircularProgressIndicator()
        }

        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop"
        )
    }
}
