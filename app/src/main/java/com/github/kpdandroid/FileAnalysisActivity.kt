package com.github.kpdandroid

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.kpdandroid.ui.DualBottomMenu
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.screens.FileAnalysisScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.ui.viewmodels.FileAnalysisViewModel
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.detection.DetectionLogger
import com.github.kpdandroid.utils.detection.KeypointDetectionAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "FileAnalysisActivity"

private const val IMAGE_MIME = "image/*"
private const val TEXT_MIME = "text/*"
private const val SUGGESTED_LOG_FILENAME = "log.txt"
private const val APPEND_MODE = "wa"

class FileAnalysisActivity : ComponentActivity() {
    private val viewModel by viewModels<FileAnalysisViewModel>()
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = (application as KeypointDetectApp).preferencesManager

        viewModel.keypointDetector = KeypointDetectionAlgorithm.constructDetectorFrom(
            algorithmName = preferencesManager.selectedFileAlgorithmName,
            context = this,
            width = viewModel.keypointDetector?.width ?: 0,
            height = viewModel.keypointDetector?.height ?: 0
        )

        initLayout()
    }

    private fun initLayout() {
        setContent {
            KeypointDetectAppTheme {
                viewModel.setKeypointColor(MaterialTheme.colors.primary)

                FileAnalysisScreen(
                    imageLayers = viewModel.imageLayers?.toList() ?: emptyList(),
                    calcTimesMs = viewModel.calcTimesMs,
                    detectionProgress = viewModel.detectionProgress,
                    showFab = viewModel.imageLayers != null &&
                        preferencesManager.selectedFileAlgorithmName !=
                        KeypointDetectionAlgorithm.NONE.formattedName,
                    onStartClick = viewModel::startDetection,
                    onStopClick = {
                        lifecycleScope.launch(Dispatchers.Default) { viewModel.stopDetection() }
                    },
                    bottomMenu = {
                        DualBottomMenu(
                            horizontalArrangement = Arrangement.Center,
                            spacing = 70.dp,
                            startItems = {
                                ExpandableBottomMenuItem(
                                    options = KeypointDetectionAlgorithm.names,
                                    selectedOption = preferencesManager.selectedFileAlgorithmName,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = "Detection algorithm"
                                        )
                                    },
                                    onSelected = this@FileAnalysisActivity::onAlgorithmSelected
                                )
                            },
                            endItems = {
                                val imagePicker = rememberLauncherForActivityResult(
                                    ActivityResultContracts.OpenDocument()
                                ) { uri ->
                                    if (uri != null) {
                                        readImage(uri)
                                    } else {
                                        Log.d(TAG, "No image picked.")
                                    }
                                }
                                val logPicker = rememberLauncherForActivityResult(
                                    ActivityResultContracts.CreateDocument(TEXT_MIME)
                                ) { uri ->
                                    if (uri != null) {
                                        setupLogger(uri)
                                    } else {
                                        Log.d(TAG, "No log file picked.")
                                    }
                                }

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
                )
            }
        }
    }

    private fun onAlgorithmSelected(algorithmName: String) {
        preferencesManager.selectedFileAlgorithmName = algorithmName
        viewModel.keypointDetector =
            KeypointDetectionAlgorithm.constructDetectorFrom(
                algorithmName = algorithmName,
                context = this@FileAnalysisActivity,
                width = viewModel.keypointDetector?.width ?: 0,
                height = viewModel.keypointDetector?.height ?: 0
            )
    }

    private fun setupLogger(fileUri: Uri) {
        Log.i(TAG, "Setting up logger to $fileUri.")

        val logOutputStream = contentResolver.openOutputStream(fileUri, APPEND_MODE)

        if (logOutputStream != null) {
            viewModel.logger = DetectionLogger(logOutputStream)
        } else {
            viewModel.logger = null
            Log.e(TAG, "Failed to open and start logging to $fileUri.")
        }
    }

    private fun readImage(imageUri: Uri) {
        Log.i(TAG, "Reading image from $imageUri.")

        val image = contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it)?.asImageBitmap()
        }

        if (image != null) {
            viewModel.provideImage(image)
        } else {
            Log.e(TAG, "Failed to open and decode image from $imageUri.")
        }
    }
}
