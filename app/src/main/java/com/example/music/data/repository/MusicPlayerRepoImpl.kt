package com.example.music.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.music.domain.model.AudioMetaData
import com.example.music.domain.repository.MusicPlayerRepository
import com.example.music.util.audio.MetaDataHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [MusicPlayerRepository]
 */
class MusicPlayerRepoImpl @Inject constructor(
    private val metaDataHelper: MetaDataHelper
) : MusicPlayerRepository {
    override suspend fun getAudios(): List<AudioMetaData> {
        return withContext(Dispatchers.IO) {
            metaDataHelper.getAudios()
        }
    }

    override suspend fun getCover(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            metaDataHelper.getCover(context, uri)
        }
    }
}