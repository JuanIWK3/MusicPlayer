package com.example.music.util.audio

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.example.music.domain.model.AudioMetaData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Helper class to get the metadata of the audio
 */
class MetaDataHelper @Inject constructor(@ApplicationContext var context: Context) {

    // Cursor is used to get the data from the content provider
    private var cursor: Cursor? = null

    // Projection are the columns that we want to get from the audio
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST,
    )

    // Selection clause is used to filter the data
    private var selectionClause: String? = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"

    // Selection args are the values of the selection clause
    private var selectionArgs = arrayOf("1")

    // Sort order is used to sort the data
    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    /**
     * Get all the audios from the device
     */
    @WorkerThread
    fun getAudios(): List<AudioMetaData> {
        return getCursor()
    }

    /**
     * Get the cover of the audio
     */
    @WorkerThread
    fun getCover(context: Context, uri: Uri): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val data = mmr.embeddedPicture
        mmr.release()

        return if (data != null) {
            Bitmap.createBitmap(BitmapFactory.decodeByteArray(data, 0, data.size))
        } else {
            null
        }
    }

    /**
     * Get the cursor from the content provider
     */
    private fun getCursor(): MutableList<AudioMetaData> {
        val audioList = mutableListOf<AudioMetaData>()

        // Get the cursor from the content provider
        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArgs,
            sortOrder
        )

        // Get the data from the cursor
        cursor?.let { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(projection[0])
            val durationColumn = cursor.getColumnIndexOrThrow(projection[2])
            val titleColumn = cursor.getColumnIndexOrThrow(projection[3])
            val artistColumn = cursor.getColumnIndexOrThrow(projection[5])

            // Add the data to the list
            cursor.apply {
                if (count > 0) {
                    while (moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val duration = cursor.getInt(durationColumn)
                        val title = cursor.getString(titleColumn)
                        val artist = cursor.getString(artistColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        audioList.add(
                            AudioMetaData(
                                id = id,
                                title = title,
                                artist = artist,
                                duration = duration,
                                contentUri = contentUri,
                                cover = null
                            )
                        )
                    }
                }
            }
        }

        return audioList
    }
}