package com.github.kpdandroid.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

private const val TAG = "PreferencesManager"
private const val PREFERENCES_FILE_NAME = "ALGORITHM_PREFERENCES"

private const val ALGORITHM_KEY = "algorithm"
private val ALGORITHM_DEFAULT = KeypointDetectionAlgorithm.NONE.formattedName

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

    var selectedAlgorithmName: String
        get() = preferences.getString(ALGORITHM_KEY, ALGORITHM_DEFAULT) ?: ALGORITHM_DEFAULT
        set(value) {
            preferences.edit {
                putString(ALGORITHM_KEY, value)
                apply()
            }
            Log.i(TAG, "$value algorithm was selected.")
        }
}
