package com.github.kpdandroid.ui.screens

import android.util.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.kpdandroid.ui.DetectionResultColumn
import com.github.kpdandroid.ui.ExpandableBottomMenuItem
import com.github.kpdandroid.ui.ExpandableBottomMenuItemContent
import com.github.kpdandroid.ui.UnitedBottomMenu
import com.github.kpdandroid.ui.viewmodels.CameraAnalysisViewModel
import com.github.kpdandroid.utils.detection.DetectionAlgo

@Composable
// - Not using `vm = viewModel()` as the ViewModel has a non-trivial constructor and, furthermore,
// we need to use exactly the same instance as the MainActivity does.
// - Not using `vm = viewModel(LocalContext.current as ViewModelStoreOwner)` as it may cause
// unexpected cast exceptions.
fun CameraAnalysisScreen(
    vm: CameraAnalysisViewModel,
    supportedResolutions: List<String>,
    onResolutionSelected: () -> Unit
) {
    Scaffold(
        bottomBar = {
            val context = LocalContext.current

            CameraAnalysisMenu(
                algorithmsItem = ExpandableBottomMenuItemContent(
                    options = DetectionAlgo.titles,
                    selectedOption = vm.prefs.cameraAlgoTitle,
                    onSelected = { algoTitle ->
                        vm.prefs.cameraAlgoTitle = algoTitle
                        vm.keypointDetector = DetectionAlgo.constructDetectorFrom(
                            algoTitle = algoTitle,
                            context = context,
                            width = vm.keypointDetector?.width ?: 0,
                            height = vm.keypointDetector?.height ?: 0
                        )
                    }
                ),
                resolutionsItem = ExpandableBottomMenuItemContent(
                    options = supportedResolutions,
                    selectedOption = vm.prefs.resolution?.toString() ?: "Pick resolution",
                    onSelected = {
                        vm.prefs.resolution = Size.parseSize(it)
                        onResolutionSelected()
                    }
                )
            )
        }
    ) { paddingValues ->
        if (!vm.isCameraPermissionGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera permission required")
            }
            return@Scaffold
        }

        DetectionResultColumn(
            imageLayers = vm.imageLayers?.toList() ?: emptyList(),
            altText = "Snapshot cannot be displayed",
            captions = listOf(
                vm.calcTimeMs?.let { "Latest detection time: $it ms" }
                    ?: "Pick an algorithm to see detection time"
            ),
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CameraAnalysisMenu(
    algorithmsItem: ExpandableBottomMenuItemContent,
    resolutionsItem: ExpandableBottomMenuItemContent
) {
    UnitedBottomMenu(horizontalArrangement = Arrangement.SpaceEvenly) {
        ExpandableBottomMenuItem(
            content = algorithmsItem,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Detection algorithm"
                )
            }
        )

        ExpandableBottomMenuItem(
            content = resolutionsItem,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Camera resolution"
                )
            }
        )
    }
}
