package com.example.music.domain.model

import android.graphics.Bitmap
import android.net.Uri

data class AudioMetaData(
    val contentUri: Uri,
    val id: Long,
    val cover: Bitmap?,
    val title: String,
    val artist: String,
    val duration: Int,
) {
    companion object {
        fun emptyMetadata(): AudioMetaData {
            return AudioMetaData(
                contentUri = Uri.EMPTY,
                id = 0L,
                cover = null,
                title = "",
                artist = "",
                duration = 0,
            )
        }
    }

}