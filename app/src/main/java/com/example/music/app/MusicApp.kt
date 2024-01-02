package com.example.music.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        println("MusicApp.onCreate")
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}
