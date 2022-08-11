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
import com.github.kpdandroid.ui.BottomMenuItem
import com.github.kpdandroid.ui.DualBottomMenu
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.screens.FileAnalysisScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.ui.viewmodels.FileAnalysisViewModel
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.detection.KeypointDetectionAlgorithm

private const val TAG = "FileAnalysisActivity"

private const val IMAGE_MIME = "image/*"

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
                    onStopClick = viewModel::stopDetection,
                    bottomMenu = {
                        DualBottomMenu(
                            horizontalArrangement = Arrangement.Center,
                            spacing = 70.dp,
                            startItems = {
                                ExpandableBottomMenuItem(
                                    options = KeypointDetectionAlgorithm.names,
                                    selectedOption = preferencesManager.selectedFileAlgorithmName,
                                    onSelected = this@FileAnalysisActivity::onAlgorithmSelected,
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
                                ) {
                                    it?.let { uri -> readImage(uri) }
                                        ?: Log.d(TAG, "No images picked.")
                                }

                                BottomMenuItem(
                                    title = "Files…",
                                    onClicked = { imagePicker.launch(arrayOf(IMAGE_MIME)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = "File picking"
                                        )
                                    }
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

    private fun readImage(imageUri: Uri) {
        Log.i(TAG, "Reading image from $imageUri.")

        val image = contentResolver.openInputStream(imageUri).use {
            BitmapFactory.decodeStream(it)?.asImageBitmap()
        }

        if (image != null) {
            viewModel.provideImage(image)
        } else {
            Log.e(TAG, "Failed to decode image from $imageUri.")
        }
    }
}
