package com.multimediaplayer.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.multimediaplayer.models.MediaItem
import java.io.File

class MediaScanner(private val context: Context) {
    
    fun scanAudioFiles(): List<MediaItem> {
        val audioFiles = mutableListOf<MediaItem>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.DATA} LIKE ? OR ${MediaStore.Audio.Media.DATA} LIKE ? OR ${MediaStore.Audio.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%.mp3%", "%.wav%", "%.flac%")
        
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val path = it.getString(dataColumn)
                val duration = it.getLong(durationColumn)
                val size = it.getLong(sizeColumn)
                val dateAdded = it.getLong(dateAddedColumn) * 1000L // Convert to milliseconds
                
                audioFiles.add(
                    MediaItem(
                        id = id,
                        title = name,
                        path = path,
                        type = "audio",
                        duration = duration,
                        size = size,
                        dateAdded = dateAdded
                    )
                )
            }
        }
        
        return audioFiles
    }
    
    fun scanVideoFiles(): List<MediaItem> {
        val videoFiles = mutableListOf<MediaItem>()
        
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Video.Media.DATA} LIKE ? OR ${MediaStore.Video.Media.DATA} LIKE ? OR ${MediaStore.Video.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%.mp4%", "%.mov%", "%.avi%")
        
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val path = it.getString(dataColumn)
                val duration = it.getLong(durationColumn)
                val size = it.getLong(sizeColumn)
                val dateAdded = it.getLong(dateAddedColumn) * 1000L // Convert to milliseconds
                
                videoFiles.add(
                    MediaItem(
                        id = id,
                        title = name,
                        path = path,
                        type = "video",
                        duration = duration,
                        size = size,
                        dateAdded = dateAdded
                    )
                )
            }
        }
        
        return videoFiles
    }
    
    fun scanImageFiles(): List<MediaItem> {
        val imageFiles = mutableListOf<MediaItem>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Images.Media.DATA} LIKE ? OR ${MediaStore.Images.Media.DATA} LIKE ? OR ${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%.jpg%", "%.jpeg%", "%.png%")
        
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val path = it.getString(dataColumn)
                val size = it.getLong(sizeColumn)
                val dateAdded = it.getLong(dateAddedColumn) * 1000L // Convert to milliseconds
                
                imageFiles.add(
                    MediaItem(
                        id = id,
                        title = name,
                        path = path,
                        type = "image",
                        size = size,
                        dateAdded = dateAdded
                    )
                )
            }
        }
        
        return imageFiles
    }
}