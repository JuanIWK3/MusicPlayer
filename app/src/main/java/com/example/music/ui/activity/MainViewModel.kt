package com.example.music.ui.activity

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.AudioMetaData
import com.example.music.domain.repository.MusicPlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MusicPlayerRepository
) : ViewModel() {

    private var _state by mutableStateOf(AudioPlayerState())
    val state: AudioPlayerState
        get() = _state

    private var _player: MediaPlayer? = null

    init {
        loadMedias()
    }

    fun onEvent(event: AudioPlayerEvent) {
        when (event) {
            is AudioPlayerEvent.InitAudio -> initAudio(
                event.audio,
                event.context,
                onAudioInitialized = event.onAudioInitialized
            )

            is AudioPlayerEvent.SeekTo -> seekTo(event.position)
            is AudioPlayerEvent.Play -> play()
            is AudioPlayerEvent.Pause -> pause()
            is AudioPlayerEvent.Stop -> stop()
            is AudioPlayerEvent.LoadMedia -> loadMedias()
        }
    }

    private fun loadMedias() {
        viewModelScope.launch {
            _state = _state.copy(isLoading = true)
            val audios = mutableListOf<AudioMetaData>()
            audios.addAll(repository.getAudios())
            _state = _state.copy(isLoading = false, audios = audios)
        }
    }

    private suspend fun prepareAudios(): List<AudioMetaData> {
        return repository.getAudios().map { audio ->
            val artist = if (audio.artist.contains("<unknown>")) "Unknown" else audio.artist
            audio.copy(artist = artist)
        }
    }

    private fun initAudio(audio: AudioMetaData, context: Context, onAudioInitialized: () -> Unit) {
        viewModelScope.launch {
            _state = _state.copy(isLoading = true)

            delay(800)

            val cover = repository.loadCoverBitmap(context, audio.contentUri)

            _state = _state.copy(
                isLoading = false,
                selectedAudio = audio.copy(cover = cover)
            )

            _player = MediaPlayer().apply {
                setDataSource(context, audio.contentUri)
                prepare()
            }

            _player?.setOnCompletionListener {
                pause()
            }

            _player?.setOnPreparedListener {
                onAudioInitialized()
            }

            _state = _state.copy(isLoading = false)
        }
    }

    private fun play() {
        _state = _state.copy(isPlaying = true)

        _player?.start()
    }

    private fun pause() {
        _state = _state.copy(isPlaying = false)

        _player?.pause()
    }

    private fun stop() {
        _player?.stop()
        _player?.reset()
        _player?.release()

        _state = _state.copy(isPlaying = false)

        _player = null
    }

    private fun seekTo(position: Float) {
        _player?.run {
            seekTo(position.toInt())
        }
    }
}