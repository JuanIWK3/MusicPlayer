package com.example.music.ui.activity

import android.content.Context
import android.media.AudioMetadata
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.AudioMetaData
import com.example.music.domain.repository.MusicPlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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

    private val _handler = Handler(Looper.getMainLooper())

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

            is AudioPlayerEvent.SeekTo -> seekTo(position = event.position)

            is AudioPlayerEvent.LikeOrNotSong -> likeOrNotSong(id = event.id)

            is AudioPlayerEvent.ShowLikedSongs -> showLikedSongs()

            is AudioPlayerEvent.Play -> play()
            is AudioPlayerEvent.Pause -> pause()
            is AudioPlayerEvent.Stop -> stop()
            is AudioPlayerEvent.HideLoadingDialog -> hideLoadingDialog()
            is AudioPlayerEvent.LoadMedia -> loadMedias()

        }
    }

    private fun showLikedSongs() {
        println("showLikedSongs")
        viewModelScope.launch {
            _state = _state.copy(showLikedSongs = !state.showLikedSongs)
        }
    }

    private fun loadMedias() {
        viewModelScope.launch {
            _state = _state.copy(isLoading = true)
            val audios = mutableStateListOf<AudioMetaData>()
            audios.addAll(prepareAudios())
            _state = _state.copy(audios = audios)

            repository.getLikedSongs().collect { likedSongs ->
                _state = _state.copy(
                    likedSongs = likedSongs,
                    isLoading = false,
                )
            }
        }
    }

    private suspend fun prepareAudios(): List<AudioMetaData> {
        var shouldShowWhatsappSongs = repository.shouldShowWhatsappSongs().first()

        var audios = repository.getAudios().map { audio ->
            val show: Boolean = repository.shouldShowWhatsappSongs().first()
            val artist = if (audio.artist.contains("<unknown>")) "Unknown artist" else audio.artist
            audio.copy(artist = artist)

        }.filter { audio ->
            if (shouldShowWhatsappSongs) {
                true
            } else {
                !audio.title.contains("AUD-")
            }
        }

        return audios
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

        _handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    _state = _state.copy(currentPosition = _player!!.currentPosition)
                    _handler.postDelayed(this, 1000)
                } catch (exp: Exception) {
                    _state = _state.copy(currentPosition = 0)
                }
            }

        }, 0)
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

    private fun hideLoadingDialog() {
        _state = _state.copy(isLoading = false)
    }

    private fun likeOrNotSong(id: Long) {
        viewModelScope.launch {
            repository.likeOrNotSong(id = id)
        }
    }
}