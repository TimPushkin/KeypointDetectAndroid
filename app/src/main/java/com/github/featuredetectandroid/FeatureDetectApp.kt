package com.github.featuredetectandroid

import android.app.Application
import com.github.featuredetectandroid.utils.PreferencesManager

class FeatureDetectApp : Application() {
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        application = this
        preferencesManager = PreferencesManager(applicationContext)
    }

    companion object {
        private var application: FeatureDetectApp? = null
        fun getPreferencesManager() = application?.preferencesManager
    }
}
