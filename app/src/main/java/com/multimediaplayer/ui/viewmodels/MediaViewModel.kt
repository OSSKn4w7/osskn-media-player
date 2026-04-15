package com.multimediaplayer.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multimediaplayer.data.repository.MediaRepository
import com.multimediaplayer.models.MediaItem
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MediaRepository(
        application.getDatabase().mediaDao(),
        application.applicationContext
    )
    
    val musicItems = mutableStateListOf<MediaItem>()
    val videoItems = mutableStateListOf<MediaItem>()
    val imageItems = mutableStateListOf<MediaItem>()
    val favoriteItems = mutableStateListOf<MediaItem>()
    
    val isLoading = mutableStateOf(false)
    
    fun loadMusic() {
        viewModelScope.launch {
            isLoading.value = true
            val items = repository.getMediaByType("audio")
            musicItems.clear()
            musicItems.addAll(items)
            isLoading.value = false
        }
    }
    
    fun loadVideos() {
        viewModelScope.launch {
            isLoading.value = true
            val items = repository.getMediaByType("video")
            videoItems.clear()
            videoItems.addAll(items)
            isLoading.value = false
        }
    }
    
    fun loadImages() {
        viewModelScope.launch {
            isLoading.value = true
            val items = repository.getMediaByType("image")
            imageItems.clear()
            imageItems.addAll(items)
            isLoading.value = false
        }
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            val items = repository.getFavoriteMedia()
            favoriteItems.clear()
            favoriteItems.addAll(items)
        }
    }
    
    fun toggleFavorite(mediaItem: MediaItem) {
        viewModelScope.launch {
            val newFavoriteState = !mediaItem.isFavorite
            repository.updateFavorite(mediaItem.id, newFavoriteState)
            
            // Update the item in the corresponding list
            when (mediaItem.type) {
                "audio" -> {
                    val index = musicItems.indexOfFirst { it.id == mediaItem.id }
                    if (index != -1) {
                        val updated = mediaItem.copy(isFavorite = newFavoriteState)
                        musicItems[index] = updated
                    }
                }
                "video" -> {
                    val index = videoItems.indexOfFirst { it.id == mediaItem.id }
                    if (index != -1) {
                        val updated = mediaItem.copy(isFavorite = newFavoriteState)
                        videoItems[index] = updated
                    }
                }
                "image" -> {
                    val index = imageItems.indexOfFirst { it.id == mediaItem.id }
                    if (index != -1) {
                        val updated = mediaItem.copy(isFavorite = newFavoriteState)
                        imageItems[index] = updated
                    }
                }
            }
            
            // Refresh favorites list
            loadFavorites()
        }
    }
    
    fun addMediaToCollection(mediaItem: MediaItem) {
        viewModelScope.launch {
            repository.addToCollection(mediaItem)
        }
    }
    
    fun removeMediaFromCollection(id: Long) {
        viewModelScope.launch {
            repository.removeFromCollection(id)
        }
    }
}