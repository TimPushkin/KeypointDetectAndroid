package com.github.kpdandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.kpdandroid.ui.screens.MainNavDestinations
import com.github.kpdandroid.ui.screens.MainNavScreen
import com.github.kpdandroid.ui.theme.KeypointDetectAppTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeypointDetectAppTheme {
                MainNavScreen(onDestinationClick = this::navigateTo)
            }
        }
    }

    private fun navigateTo(destination: MainNavDestinations) {
        Log.i(TAG, "Navigating to $destination.")
        val destinationClass = when (destination) {
            MainNavDestinations.FILE_ANALYSIS -> FileAnalysisActivity::class.java
            MainNavDestinations.CAMERA_ANALYSIS -> CameraAnalysisActivity::class.java
        }
        startActivity(Intent(this, destinationClass))
    }
}
