package com.osskn.mediaplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.osskn.mediaplayer.data.local.MediaFileDao
import com.osskn.mediaplayer.model.MediaFile
import com.osskn.mediaplayer.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MediaRepository(
    private val context: Context,
    private val mediaFileDao: MediaFileDao
) {
    fun getMusicFiles(): Flow<List<MediaFile>> {
        return mediaFileDao.getMediaByType(MediaType.AUDIO)
    }

    fun getVideoFiles(): Flow<List<MediaFile>> {
        return mediaFileDao.getMediaByType(MediaType.VIDEO)
    }

    fun getImageFiles(): Flow<List<MediaFile>> {
        return mediaFileDao.getMediaByType(MediaType.IMAGE)
    }

    fun getFavorites(): Flow<List<MediaFile>> {
        return mediaFileDao.getFavorites()
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        mediaFileDao.updateFavorite(id, isFavorite)
    }

    suspend fun scanMediaStore() {
        withContext(Dispatchers.IO) {
            val mediaFiles = mutableListOf<MediaFile>()

            // Scan audio files
            mediaFiles.addAll(scanAudio())
            // Scan video files
            mediaFiles.addAll(scanVideo())
            // Scan image files
            mediaFiles.addAll(scanImages())

            mediaFileDao.insertAll(mediaFiles)
        }
    }

    private fun scanAudio(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()

                mediaFiles.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        title = it.getString(titleColumn),
                        artist = it.getString(artistColumn),
                        album = it.getString(albumColumn),
                        duration = it.getLong(durationColumn),
                        size = it.getLong(sizeColumn),
                        mimeType = it.getString(mimeTypeColumn),
                        mediaType = MediaType.AUDIO,
                        dateAdded = it.getLong(dateAddedColumn),
                        dateModified = it.getLong(dateModifiedColumn),
                        filePath = it.getString(dataColumn)
                    )
                )
            }
        }

        return mediaFiles
    }

    private fun scanVideo(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DATA
        )

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()

                mediaFiles.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        title = it.getString(titleColumn),
                        artist = it.getString(artistColumn),
                        album = it.getString(albumColumn),
                        duration = it.getLong(durationColumn),
                        size = it.getLong(sizeColumn),
                        mimeType = it.getString(mimeTypeColumn),
                        mediaType = MediaType.VIDEO,
                        dateAdded = it.getLong(dateAddedColumn),
                        dateModified = it.getLong(dateModifiedColumn),
                        filePath = it.getString(dataColumn)
                    )
                )
            }
        }

        return mediaFiles
    }

    private fun scanImages(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATA
        )

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()

                mediaFiles.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        title = it.getString(titleColumn),
                        artist = null,
                        album = null,
                        duration = 0,
                        size = it.getLong(sizeColumn),
                        mimeType = it.getString(mimeTypeColumn),
                        mediaType = MediaType.IMAGE,
                        dateAdded = it.getLong(dateAddedColumn),
                        dateModified = it.getLong(dateModifiedColumn),
                        filePath = it.getString(dataColumn)
                    )
                )
            }
        }

        return mediaFiles
    }
}
