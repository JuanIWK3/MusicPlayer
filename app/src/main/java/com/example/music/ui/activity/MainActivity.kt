package com.example.music.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.music.app.MusicApp
import com.example.music.ui.theme.MusicTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicTheme {

            }
        }
    }
}
