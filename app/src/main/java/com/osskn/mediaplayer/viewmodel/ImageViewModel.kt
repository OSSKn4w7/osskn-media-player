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

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository = MediaRepository(
        application,
        (application as OssknMediaPlayerApp).database.mediaFileDao()
    )

    private val _imageFiles = MutableStateFlow<List<MediaFile>>(emptyList())
    val imageFiles: StateFlow<List<MediaFile>> = _imageFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadImageFiles()
    }

    private fun loadImageFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getImageFiles()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { files ->
                    _imageFiles.value = files
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
