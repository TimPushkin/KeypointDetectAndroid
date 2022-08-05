package com.github.kpdandroid

import android.app.Application
import com.github.kpdandroid.utils.PreferencesManager

class KeypointDetectApp : Application() {
    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(applicationContext)
    }
}
