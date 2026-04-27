package com.osskn.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osskn.mediaplayer.OssknMediaPlayerApp
import com.osskn.mediaplayer.data.repository.MediaRepository
import com.osskn.mediaplayer.model.MediaFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository = MediaRepository(
        application,
        (application as OssknMediaPlayerApp).database.mediaFileDao()
    )

    private val _videoFiles = MutableStateFlow<List<MediaFile>>(emptyList())
    val videoFiles: StateFlow<List<MediaFile>> = _videoFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadVideoFiles()
    }

    private fun loadVideoFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getVideoFiles()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { files ->
                    _videoFiles.value = files
                    _isLoading.value = false
                }
        }
    }

    fun toggleFavorite(mediaFile: MediaFile) {
        viewModelScope.launch {
            repository.toggleFavorite(mediaFile.id, !mediaFile.isFavorite)
        }
    }

    fun scanMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.scanMediaStore()
            _isLoading.value = false
        }
    }
}
