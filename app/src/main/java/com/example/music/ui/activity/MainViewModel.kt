package com.example.music.ui.activity

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.AudioMetaData
import com.example.music.domain.repository.MusicPlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [MainActivity]
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MusicPlayerRepository
) : ViewModel() {
    // Native MediaPlayer
    private var _player: MediaPlayer? = null

    private var _state = MutableStateFlow(AudioPlayerState())
    val state = _state.asStateFlow()

    init {
        loadMedias()
    }

    fun onEvent(event: AudioPlayerEvent) {
        when (event) {
            is AudioPlayerEvent.InitAudio -> initAudio(event.audio, event.context)
            is AudioPlayerEvent.SeekTo -> seekTo(event.position)
            is AudioPlayerEvent.Play -> play()
            is AudioPlayerEvent.Pause -> pause()
            is AudioPlayerEvent.Stop -> stop()
            is AudioPlayerEvent.LoadMedia -> loadMedias()
        }
    }

    private fun loadMedias() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val audios = mutableListOf<AudioMetaData>()
            audios.addAll(repository.getAudios())
            _state.update { it.copy(isLoading = false, audios = audios) }
        }
    }

    private suspend fun prepareAudios(): List<AudioMetaData> {
        return repository.getAudios().map { audio ->
            val artist = if (audio.artist.contains("<unknown>")) "Unknown" else audio.artist
            audio.copy(artist = artist)
        }
    }

    private fun initAudio(audio: AudioMetaData, context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            delay(800)

            val cover = repository.loadCoverBitmap(context, audio.contentUri)

            _player = MediaPlayer().apply {
                setDataSource(context, audio.contentUri)
                prepare()
            }

            _state.update { it.copy(isLoading = false) }

        }
    }

    private fun play() {
        _state.update { it.copy(isPlaying = true) }

        _player?.start()
    }

    private fun pause() {
        _state.update { it.copy(isPlaying = false) }

        _player?.pause()
    }

    private fun stop() {
        _player?.stop()
        _player?.reset()
        _player?.release()

        _state.update { it.copy(isPlaying = false) }

        _player = null
    }

    private fun seekTo(position: Float) {
        _player?.run {
            seekTo(position.toInt())
        }
    }
}