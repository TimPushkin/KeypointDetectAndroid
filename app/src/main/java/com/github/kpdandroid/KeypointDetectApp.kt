package com.github.kpdandroid

import android.app.Application
import com.github.kpdandroid.utils.PreferencesManager

class KeypointDetectApp : Application() {
    lateinit var prefs: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(applicationContext)
    }
}
