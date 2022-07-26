package com.github.featuredetectandroid.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

private const val TAG = "PreferencesManager"
private const val PREFERENCES_FILE_NAME = "ALGORITHM_PREFERENCES"
private const val ALGORITHM_KEY = "algorithm"

private val ALGORITHM_DEFAULT = KeypointDetectionAlgorithm.NONE.algorithmName

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun getSelectedAlgorithm() = preferences.getString(ALGORITHM_KEY, ALGORITHM_DEFAULT)
        ?: ALGORITHM_DEFAULT

    fun putSelectedAlgorithm(newAlgorithm: String) {
        preferences.edit {
            putString(ALGORITHM_KEY, newAlgorithm)
            apply()
        }

        Log.i(TAG, "$newAlgorithm algorithm was selected.")
    }
}
