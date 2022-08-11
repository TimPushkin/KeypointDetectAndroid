package com.github.kpdandroid.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CameraAnalysisViewModel : ImageAnalysisViewModel() {
    var calcTimeMs: Long? by mutableStateOf(null)
}
