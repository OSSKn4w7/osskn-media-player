package com.multimediaplayer.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multimediaplayer.data.repository.MediaRepository
import com.multimediaplayer.services.GitHubSyncService
import kotlinx.coroutines.launch

class GitHubViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaRepository = MediaRepository(
        application.getDatabase().mediaDao(),
        application.applicationContext
    )
    
    private val _githubService = GitHubSyncService(application.applicationContext, mediaRepository)
    
    val isAuthenticated = mutableStateOf(_githubService.isLoggedIn())
    val isSyncing = mutableStateOf(false)
    val syncProgress = mutableStateOf(0)
    val lastSyncMessage = mutableStateOf("")
    
    fun authenticateWithToken(token: String) {
        viewModelScope.launch {
            isSyncing.value = true
            lastSyncMessage.value = "Authenticating..."
            
            val result = _githubService.authenticate(token)
            result.fold(
                onSuccess = { user ->
                    isAuthenticated.value = true
                    lastSyncMessage.value = "Welcome, ${user.name ?: user.login}!"
                },
                onFailure = { error ->
                    lastSyncMessage.value = "Authentication failed: ${error.message}"
                }
            )
            
            isSyncing.value = false
        }
    }
    
    fun setupRepository(repoName: String = "multimedia-player-cloud") {
        viewModelScope.launch {
            isSyncing.value = true
            lastSyncMessage.value = "Setting up repository..."
            
            val result = _githubService.setupPrivateRepo(repoName)
            result.fold(
                onSuccess = { repo ->
                    lastSyncMessage.value = "Repository ready: ${repo.name}"
                },
                onFailure = { error ->
                    lastSyncMessage.value = "Setup failed: ${error.message}"
                }
            )
            
            isSyncing.value = false
        }
    }
    
    fun syncToCloud() {
        viewModelScope.launch {
            isSyncing.value = true
            syncProgress.value = 0
            lastSyncMessage.value = "Starting sync to cloud..."
            
            val result = _githubService.syncToCloud()
            result.fold(
                onSuccess = { count ->
                    lastSyncMessage.value = "Successfully synced $count items to cloud"
                },
                onFailure = { error ->
                    lastSyncMessage.value = "Sync failed: ${error.message}"
                }
            )
            
            isSyncing.value = false
        }
    }
    
    fun downloadFromCloud() {
        viewModelScope.launch {
            isSyncing.value = true
            lastSyncMessage.value = "Downloading from cloud..."
            
            val result = _githubService.downloadFromCloud()
            result.fold(
                onSuccess = { files ->
                    lastSyncMessage.value = "Downloaded ${files.size} items from cloud"
                },
                onFailure = { error ->
                    lastSyncMessage.value = "Download failed: ${error.message}"
                }
            )
            
            isSyncing.value = false
        }
    }
    
    fun logout() {
        _githubService.logout()
        isAuthenticated.value = false
        lastSyncMessage.value = "Logged out successfully"
    }
}