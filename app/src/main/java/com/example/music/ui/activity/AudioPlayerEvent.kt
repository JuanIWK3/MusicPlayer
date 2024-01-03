package com.example.music.ui.activity

import android.content.Context
import com.example.music.domain.model.AudioMetaData

sealed class AudioPlayerEvent {
    data class InitAudio(
        val audio: AudioMetaData,
        val context: Context
    ) : AudioPlayerEvent()

    data class SeekTo(
        val position: Float
    ) : AudioPlayerEvent()

    data object Play : AudioPlayerEvent()
    data object Pause : AudioPlayerEvent()
    data object Stop : AudioPlayerEvent()
    data object LoadMedia : AudioPlayerEvent()

}