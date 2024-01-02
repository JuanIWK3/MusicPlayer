package com.example.music.app

import android.app.Application
import androidx.compose.material3.Text
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
