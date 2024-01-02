package com.example.music.util.audio

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MetaDataHelper @Inject constructor(@ApplicationContext var context: Context) {

    private var cursor: Cursor? = null

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST,
    )

    private var selectionClause: String? = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"

    private var selectionArgs = arrayOf("1")

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    private fun getCursor(): Cursor? {
        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArgs,
            sortOrder
        )

        cursor?.let {
            val idColumn = it.getColumnIndexOrThrow(projection[0])
            val durationColumn = it.getColumnIndexOrThrow(projection[2])
            val titleColumn = it.getColumnIndexOrThrow(projection[3])
            val artistColumn = it.getColumnIndexOrThrow(projection[5])

            cursor?.apply {
                if (count > 0) {
                    while(moveToNext()) {
                        val id = getLong(idColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val artist = getString(artistColumn)
                    }
                }
            }
        }


        return cursor
    }
}