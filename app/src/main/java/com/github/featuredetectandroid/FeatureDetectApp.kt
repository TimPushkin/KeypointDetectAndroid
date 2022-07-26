package com.github.featuredetectandroid

import android.app.Application
import com.github.featuredetectandroid.utils.PreferencesManager

class FeatureDetectApp : Application() {
    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(applicationContext)
    }
}
