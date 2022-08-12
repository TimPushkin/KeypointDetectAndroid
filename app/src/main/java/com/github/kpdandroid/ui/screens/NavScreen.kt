package com.github.kpdandroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.github.kpdandroid.R

private const val ICON_SCALE = 1.5f
const val MAIN_SCREEN = "MAIN_SCREEN"

enum class NavDestination(val title: String, val icon: ImageVector) {
    FILE_ANALYSIS("File analysis", Icons.Default.Storage),
    CAMERA_ANALYSIS("Camera analysis", Icons.Default.Camera)
}

@Composable
fun NavScreen(onDestinationSelected: (NavDestination) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(NavDestination.values()) { destination ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { onDestinationSelected(destination) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = "Navigation item",
                        modifier = Modifier
                            .scale(ICON_SCALE)
                            .padding(16.dp),
                        tint = MaterialTheme.colors.primaryVariant
                    )

                    Text(destination.title)
                }
            }
        }
    }
}
