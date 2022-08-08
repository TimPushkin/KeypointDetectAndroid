package com.github.kpdandroid.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.core.content.edit

private const val TAG = "PreferencesManager"
private const val PREFERENCES_FILE_NAME = "ALGORITHM_PREFERENCES"

private const val ALGORITHM_KEY = "algorithm"
private val ALGORITHM_DEFAULT = KeypointDetectionAlgorithm.NONE.formattedName

private const val RESOLUTION_KEY = "resolution"
private val RESOLUTION_DEFAULT = null

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

    var selectedAlgorithmName: String
        get() = preferences.getString(ALGORITHM_KEY, ALGORITHM_DEFAULT) ?: ALGORITHM_DEFAULT
        set(value) {
            preferences.edit { putString(ALGORITHM_KEY, value) }
            Log.i(TAG, "Algorithm $value has been selected.")
        }

    var selectedResolution: Size?
        get() = preferences.getString(RESOLUTION_KEY, RESOLUTION_DEFAULT)
            ?.let { Size.parseSize(it) }
        set(value) {
            preferences.edit {
                putString(RESOLUTION_KEY, value?.toString())
            }
            Log.i(TAG, "Resolution ${value?.width} x ${value?.height} has been selected.")
        }
}
