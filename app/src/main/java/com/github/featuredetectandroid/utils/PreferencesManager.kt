package com.github.featuredetectandroid.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.github.featuredetectandroid.MainActivity

private const val TAG = "PreferencesManager"
private const val KEY = "algorithm"
private const val NONE = "None"

class PreferencesManager(mainActivity: MainActivity) {
    private val preferences = mainActivity.getPreferences(Context.MODE_PRIVATE)

    fun getAlgorithm() = preferences.getString(KEY, NONE) ?: NONE

    fun selectAlgorithm(newAlgorithm: String) {
        preferences.edit {
            putString(KEY, newAlgorithm)
            apply()
        }

        Log.i(TAG, "$newAlgorithm algorithm was selected.")
    }
}
