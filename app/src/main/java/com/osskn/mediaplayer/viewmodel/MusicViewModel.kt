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

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository = MediaRepository(
        application,
        (application as OssknMediaPlayerApp).database.mediaFileDao()
    )

    private val _musicFiles = MutableStateFlow<List<MediaFile>>(emptyList())
    val musicFiles: StateFlow<List<MediaFile>> = _musicFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPlaying = MutableStateFlow<MediaFile?>(null)
    val currentPlaying: StateFlow<MediaFile?> = _currentPlaying.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    init {
        loadMusicFiles()
    }

    private fun loadMusicFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMusicFiles()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { files ->
                    _musicFiles.value = files
                    _isLoading.value = false
                }
        }
    }

    fun playMusic(mediaFile: MediaFile) {
        _currentPlaying.value = mediaFile
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleFavorite(mediaFile: MediaFile) {
        viewModelScope.launch {
            repository.toggleFavorite(mediaFile.id, !mediaFile.isFavorite)
        }
    }

    fun seekTo(progress: Float) {
        _progress.value = progress
    }

    fun scanMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.scanMediaStore()
            _isLoading.value = false
        }
    }
}
