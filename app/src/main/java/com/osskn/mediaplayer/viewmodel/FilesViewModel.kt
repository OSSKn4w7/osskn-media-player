package com.osskn.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osskn.mediaplayer.OssknMediaPlayerApp
import com.osskn.mediaplayer.data.local.CustomFolderDao
import com.osskn.mediaplayer.data.repository.MediaRepository
import com.osskn.mediaplayer.model.CustomFolder
import com.osskn.mediaplayer.model.MediaFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File

class FilesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository = MediaRepository(
        application,
        (application as OssknMediaPlayerApp).database.mediaFileDao()
    )

    private val folderDao: CustomFolderDao =
        (application as OssknMediaPlayerApp).database.customFolderDao()

    private val _folders = MutableStateFlow<List<CustomFolder>>(emptyList())
    val folders: StateFlow<List<CustomFolder>> = _folders.asStateFlow()

    private val _files = MutableStateFlow<List<MediaFile>>(emptyList())
    val files: StateFlow<List<MediaFile>> = _files.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            folderDao.getAllFolders()
                .catch { e ->
                    // 错误处理
                }
                .collect { folderList ->
                    _folders.value = folderList
                }
        }
    }

    fun createFolder(name: String, path: String) {
        viewModelScope.launch {
            val folder = CustomFolder(name = name, path = path)
            folderDao.insertFolder(folder)

            // 创建物理目录
            val directory = File(path)
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }
    }

    fun deleteFolder(folder: CustomFolder) {
        viewModelScope.launch {
            folderDao.deleteFolder(folder)
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
