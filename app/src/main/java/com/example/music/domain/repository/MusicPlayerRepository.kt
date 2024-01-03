package com.example.music.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.music.domain.model.AudioMetaData

interface MusicPlayerRepository {
    suspend fun getAudios(): List<AudioMetaData>
    suspend fun getCover(context: Context, uri: Uri): Bitmap?
}