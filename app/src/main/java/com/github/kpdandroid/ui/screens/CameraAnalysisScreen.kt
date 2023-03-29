package com.github.kpdandroid.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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

// - Not using `vm = viewModel()` as we need to use exactly the same instance as the MainActivity
// does.
// - Not using `vm = viewModel(LocalContext.current as ViewModelStoreOwner)` as it may cause
// unexpected cast exceptions.
@Composable
fun CameraAnalysisScreen(
    vm: CameraAnalysisViewModel,
    supportedResolutions: List<String>,
    onResolutionSelected: () -> Unit
) {
    Scaffold(
        bottomBar = {
            CameraAnalysisMenu(
                algorithmsItem = ExpandableBottomMenuItemContent(
                    options = DetectionAlgo.titles,
                    selectedOption = vm.prefs.cameraAlgoTitle,
                    onSelected = { algoTitle -> vm.prefs.cameraAlgoTitle = algoTitle }
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
        if (vm.isCameraPermissionGranted) {
            DetectionResultColumn(
                imageLayers = vm.imageLayers?.toList() ?: emptyList(),
                altText = "Snapshot cannot be displayed",
                captions = listOf(
                    vm.calcTimeMs?.let { "Latest detection time: $it ms" }
                        ?: "Pick an algorithm to see detection time"
                ),
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                Text("Camera permission required")
                TextButton(onClick = { showAppSettingsScreen(context) }) {
                    Text("Open settings")
                }
            }
        }
    }
}

private fun showAppSettingsScreen(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    )
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
