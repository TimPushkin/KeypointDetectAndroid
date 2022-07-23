package com.github.featuredetectandroid.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit

private const val TAG = "PreferencesManager"
private const val ALGORITHM_KEY = "algorithm"
private const val ALGORITHM_DEFAULT = "None"

class PreferencesManager(activity: Activity) {
    private val preferences = activity.getPreferences(Context.MODE_PRIVATE)

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
