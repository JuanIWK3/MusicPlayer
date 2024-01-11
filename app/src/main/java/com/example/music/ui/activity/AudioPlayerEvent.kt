package com.example.music.ui.activity

import android.content.Context
import com.example.music.domain.model.AudioMetaData

sealed class AudioPlayerEvent {
    data class InitAudio(
        val audio: AudioMetaData,
        val context: Context,
        val onAudioInitialized: () -> Unit
    ) : AudioPlayerEvent()

    data class SeekTo(
        val position: Float
    ) : AudioPlayerEvent()

    data class LikeOrNotSong(
        val id: Long
    ): AudioPlayerEvent()

    data object Play : AudioPlayerEvent()
    data object Pause : AudioPlayerEvent()
    data object Stop : AudioPlayerEvent()
    data object HideLoadingDialog: AudioPlayerEvent()
    data object LoadMedia : AudioPlayerEvent()
}