package com.example.music.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.music.domain.model.AudioMetaData
import com.example.music.domain.repository.MusicPlayerRepository
import com.example.music.util.audio.MetaDataHelper
import com.example.music.util.audio.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [MusicPlayerRepository]
 */
class MusicPlayerRepoImpl @Inject constructor(
    private val metaDataHelper: MetaDataHelper,
    private val userPreferences: UserPreferences
) : MusicPlayerRepository {
    override suspend fun getAudios(): List<AudioMetaData> {
        return withContext(Dispatchers.IO) {
            metaDataHelper.getAudios()
        }
    }

    override suspend fun loadCoverBitmap(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            metaDataHelper.getCover(context, uri)
        }
    }

    override suspend fun likeOrNotSong(id: Long) {
        withContext(Dispatchers.IO){
            userPreferences.likeOrNotSong(id = id)
        }
    }

    override fun getLikedSongs(): Flow<List<Long>> {
        return userPreferences.likedSongs
    }
}