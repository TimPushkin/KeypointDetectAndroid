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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.kpdandroid.ui.BottomMenuItem
import com.github.kpdandroid.ui.DualBottomMenu
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.ImageAnalysisViewModel
import com.github.kpdandroid.ui.screens.FileAnalysisScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme
import com.github.kpdandroid.utils.PreferencesManager
import com.github.kpdandroid.utils.bitmapToRgbBytes
import com.github.kpdandroid.utils.detection.KeypointDetectionAlgorithm
import com.github.kpdandroid.utils.detection.detectTimedRepeated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "FileAnalysisActivity"

private const val IMAGE_MIME = "image/*"

class FileAnalysisActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageAnalysisViewModel>()
    private val dafaultScope = CoroutineScope(Dispatchers.Default)
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
                    calcTimeMs = viewModel.calcTimeMs,
                    showStartButton = viewModel.imageLayers != null &&
                        preferencesManager.selectedFileAlgorithmName !=
                        KeypointDetectionAlgorithm.NONE.formattedName,
                    onStartClick = this::runDetection,
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

    private fun runDetection(times: Int) {
        viewModel.keypointDetector?.let { detector ->
            viewModel.imageLayers?.let { (image, _) ->
                Log.i(TAG, "Running detection for $times times.")

                dafaultScope.launch {
                    val rgbBytes = bitmapToRgbBytes(image.asAndroidBitmap())
                    val results = detector.detectTimedRepeated(
                        rgbBytes = rgbBytes,
                        imageWidth = image.width,
                        imageHeight = image.height,
                        times = times
                    )

                    for ((keypoints, meanCalcTimeMs, devCalcTimeMs) in results) {
                        launch(Dispatchers.Main) {
                            viewModel.drawKeypoints(keypoints)
                            viewModel.calcTimeMs = meanCalcTimeMs to devCalcTimeMs
                        }
                    }
                }
            } ?: run { Log.e(TAG, "Cannot run detection: image is not selected.") }
        } ?: run { Log.e(TAG, "Cannot run detection: detector is not selected.") }
    }
}
