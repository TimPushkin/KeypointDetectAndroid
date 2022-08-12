package com.github.kpdandroid.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.github.kpdandroid.utils.detection.DetectionAlgo

private const val TAG = "PreferencesManager"

private const val PREFERENCES_FILE_NAME = "preferences"

private const val FILE_ALGO_KEY = "file_algorithm"
private const val CAMERA_ALGO_KEY = "camera_algorithm"
private val ALGO_DEFAULT = DetectionAlgo.NONE.title

private const val RESOLUTION_KEY = "resolution"
private val RESOLUTION_DEFAULT = null

class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

    private var fileAlgoNameState by mutableStateOf(
        prefs.getString(FILE_ALGO_KEY, ALGO_DEFAULT) ?: ALGO_DEFAULT
    )
    var fileAlgoName: String
        get() = fileAlgoNameState
        set(value) {
            prefs.edit { putString(FILE_ALGO_KEY, value) }
            fileAlgoNameState = value
            Log.i(TAG, "Algorithm $value has been selected for file analysis.")
        }

    private var cameraAlgoNameState by mutableStateOf(
        prefs.getString(CAMERA_ALGO_KEY, ALGO_DEFAULT) ?: ALGO_DEFAULT
    )
    var cameraAlgoName: String
        get() = cameraAlgoNameState
        set(value) {
            prefs.edit { putString(CAMERA_ALGO_KEY, value) }
            cameraAlgoNameState = value
            Log.i(TAG, "Algorithm $value has been selected for camera analysis.")
        }

    private var resolutionState by mutableStateOf(
        prefs.getString(RESOLUTION_KEY, RESOLUTION_DEFAULT)?.let { Size.parseSize(it) }
    )
    var resolution: Size?
        get() = resolutionState
        set(value) {
            prefs.edit { putString(RESOLUTION_KEY, value?.toString()) }
            resolutionState = value
            Log.i(TAG, "Resolution $value has been selected.")
        }
}
