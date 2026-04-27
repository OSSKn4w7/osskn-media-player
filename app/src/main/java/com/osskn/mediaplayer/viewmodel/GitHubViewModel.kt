package com.osskn.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osskn.mediaplayer.data.repository.GitHubRepository
import com.osskn.mediaplayer.data.remote.GitHubUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class GitHubViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GitHubRepository()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<GitHubUser?>(null)
    val user: StateFlow<GitHubUser?> = _user.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(token: String) {
        viewModelScope.launch {
            repository.setToken(token)
            _isLoggedIn.value = true

            val result = repository.getUserInfo()
            result.fold(
                onSuccess = { userInfo ->
                    _user.value = userInfo

                    val repoExists = repository.checkRepositoryExists(userInfo.login)
                    repoExists.fold(
                        onSuccess = { exists ->
                            if (!exists) {
                                repository.createBackupRepository()
                            }
                        },
                        onFailure = { e ->
                            _errorMessage.value = e.message
                        }
                    )
                },
                onFailure = { e ->
                    _isLoggedIn.value = false
                    _errorMessage.value = e.message
                }
            )
        }
    }

    fun logout() {
        repository.clearToken()
        _isLoggedIn.value = false
        _user.value = null
    }

    fun uploadFiles(files: List<File>) {
        if (_user.value == null) return

        viewModelScope.launch {
            _isUploading.value = true
            _uploadProgress.value = 0f

            val owner = _user.value!!.login
            val repo = "media-backup"
            val totalFiles = files.size

            files.forEachIndexed { index, file ->
                repository.uploadFile(owner, repo, file.name, file)
                _uploadProgress.value = (index + 1).toFloat() / totalFiles
            }

            _isUploading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
