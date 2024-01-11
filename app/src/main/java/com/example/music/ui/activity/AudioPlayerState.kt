package com.example.music.ui.activity

import com.example.music.domain.model.AudioMetaData

data class AudioPlayerState (
    val isLoading: Boolean = false,
    val audios: List<AudioMetaData> = emptyList(),
    val selectedAudio: AudioMetaData = AudioMetaData.emptyMetadata(),
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val likedSongs: List<Long> = emptyList()
)