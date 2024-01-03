package com.example.music.app

import android.app.Application
import androidx.compose.material3.Text
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp() : Application() {
    override fun onCreate() { // onCreate is called when the application is started
        super.onCreate() // super.onCreate is used to call the parent class's onCreate method
        println("MusicApp.onCreate")
    }

    override fun onTerminate() { // onTerminate is called when the application is terminated
        super.onTerminate() // super.onTerminate is used to call the parent class's onTerminate method
    }
}
