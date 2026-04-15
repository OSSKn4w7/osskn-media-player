package com.multimediaplayer.data.repository

import android.content.Context
import com.multimediaplayer.data.local.MediaDao
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.utils.MediaScanner

class MediaRepository(
    private val mediaDao: MediaDao,
    private val context: Context
) {
    private val mediaScanner = MediaScanner(context)
    
    suspend fun getAllMedia(): List<MediaItem> {
        // First, get media from database
        val dbMedia = mediaDao.getRecentMedia(100) // Limit to recent 100 items
        
        // Then scan device for new media
        val allDeviceMedia = mutableListOf<MediaItem>()
        allDeviceMedia.addAll(mediaScanner.scanAudioFiles())
        allDeviceMedia.addAll(mediaScanner.scanVideoFiles())
        allDeviceMedia.addAll(mediaScanner.scanImageFiles())
        
        // Update database with new items
        for (deviceMedia in allDeviceMedia) {
            val exists = dbMedia.any { it.path == deviceMedia.path }
            if (!exists) {
                mediaDao.insertMedia(deviceMedia)
            }
        }
        
        return mediaDao.getRecentMedia(100) // Return updated list
    }
    
    suspend fun getMediaByType(type: String): List<MediaItem> {
        // Get from DB
        val dbMedia = mediaDao.getMediaByType(type)
        
        // Scan for new media of this type
        val deviceMedia = when (type.lowercase()) {
            "audio" -> mediaScanner.scanAudioFiles()
            "video" -> mediaScanner.scanVideoFiles()
            "image" -> mediaScanner.scanImageFiles()
            else -> emptyList()
        }
        
        // Add new items to DB
        for (deviceItem in deviceMedia) {
            val exists = dbMedia.any { it.path == deviceItem.path }
            if (!exists) {
                mediaDao.insertMedia(deviceItem)
            }
        }
        
        return mediaDao.getMediaByType(type)
    }
    
    suspend fun getFavoriteMedia(): List<MediaItem> {
        return mediaDao.getFavoriteMedia()
    }
    
    suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        mediaDao.updateFavorite(id, isFavorite)
    }
    
    suspend fun addToCollection(mediaItem: MediaItem) {
        mediaDao.insertMedia(mediaItem)
    }
    
    suspend fun removeFromCollection(id: Long) {
        mediaDao.deleteMediaById(id)
    }
}