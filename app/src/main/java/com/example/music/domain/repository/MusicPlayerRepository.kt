package com.example.music.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.music.domain.model.AudioMetaData
import kotlinx.coroutines.flow.Flow

/**
 * Repository for the music player
 */
interface MusicPlayerRepository {
    /**
     * Get all the audios from the device
     */
    suspend fun getAudios(): List<AudioMetaData>

    /**
     * Get the cover of the audio
     */
    suspend fun loadCoverBitmap(context: Context, uri: Uri): Bitmap?

    suspend fun likeOrNotSong(id: Long)

    fun getLikedSongs(): Flow<List<Long>>

    fun shouldShowWhatsappSongs(): Flow<Boolean>
}

