package com.github.kpdandroid.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.github.kpdandroid.utils.detection.KeypointDetectionAlgorithm

private const val TAG = "PreferencesManager"

private const val PREFERENCES_FILE_NAME = "preferences"

private const val FILE_ALGORITHM_KEY = "file_algorithm"
private const val CAMERA_ALGORITHM_KEY = "camera_algorithm"
private val ALGORITHM_DEFAULT = KeypointDetectionAlgorithm.NONE.formattedName

private const val RESOLUTION_KEY = "resolution"
private val RESOLUTION_DEFAULT = null

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

    private var selectedFileAlgorithmNameState by mutableStateOf(
        preferences.getString(FILE_ALGORITHM_KEY, ALGORITHM_DEFAULT) ?: ALGORITHM_DEFAULT
    )
    var selectedFileAlgorithmName: String
        get() = selectedFileAlgorithmNameState
        set(value) {
            preferences.edit { putString(FILE_ALGORITHM_KEY, value) }
            selectedFileAlgorithmNameState = value
            Log.i(TAG, "Algorithm $value has been selected for file analysis.")
        }

    private var selectedCameraAlgorithmNameState by mutableStateOf(
        preferences.getString(CAMERA_ALGORITHM_KEY, ALGORITHM_DEFAULT) ?: ALGORITHM_DEFAULT
    )
    var selectedCameraAlgorithmName: String
        get() = selectedCameraAlgorithmNameState
        set(value) {
            preferences.edit { putString(CAMERA_ALGORITHM_KEY, value) }
            selectedCameraAlgorithmNameState = value
            Log.i(TAG, "Algorithm $value has been selected for camera analysis.")
        }

    private var selectedResolutionState by mutableStateOf(
        preferences.getString(RESOLUTION_KEY, RESOLUTION_DEFAULT)?.let { Size.parseSize(it) }
    )
    var selectedResolution: Size?
        get() = selectedResolutionState
        set(value) {
            preferences.edit { putString(RESOLUTION_KEY, value?.toString()) }
            selectedResolutionState = value
            Log.i(TAG, "Resolution $value has been selected.")
        }
}
