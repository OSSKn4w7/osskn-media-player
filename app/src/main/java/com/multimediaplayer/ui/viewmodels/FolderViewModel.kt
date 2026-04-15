package com.multimediaplayer.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multimediaplayer.data.models.Folder
import com.multimediaplayer.data.repository.FolderRepository
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FolderRepository(
        application.getDatabase().folderDao()
    )
    
    val folders = mutableStateListOf<Folder>()
    val audioFolders = mutableStateListOf<Folder>()
    val videoFolders = mutableStateListOf<Folder>()
    val imageFolders = mutableStateListOf<Folder>()
    val favoriteFolders = mutableStateListOf<Folder>()
    
    val isLoading = mutableStateOf(false)
    
    fun loadAllFolders() {
        viewModelScope.launch {
            isLoading.value = true
            val allFolders = repository.getAllFolders()
            folders.clear()
            folders.addAll(allFolders)
            
            // Separate by type
            audioFolders.clear()
            videoFolders.clear()
            imageFolders.clear()
            favoriteFolders.clear()
            
            allFolders.forEach { folder ->
                when (folder.mediaType) {
                    "audio" -> audioFolders.add(folder)
                    "video" -> videoFolders.add(folder)
                    "image" -> imageFolders.add(folder)
                }
                if (folder.isFavorite) {
                    favoriteFolders.add(folder)
                }
            }
            isLoading.value = false
        }
    }
    
    fun loadFoldersByType(type: String) {
        viewModelScope.launch {
            isLoading.value = true
            val typeFolders = repository.getFoldersByType(type)
            when (type) {
                "audio" -> audioFolders.clearAndAddAll(typeFolders)
                "video" -> videoFolders.clearAndAddAll(typeFolders)
                "image" -> imageFolders.clearAndAddAll(typeFolders)
            }
            isLoading.value = false
        }
    }
    
    fun loadFavoriteFolders() {
        viewModelScope.launch {
            isLoading.value = true
            val favFolders = repository.getFavoriteFolders()
            favoriteFolders.clear()
            favoriteFolders.addAll(favFolders)
            isLoading.value = false
        }
    }
    
    fun createNewFolder(name: String, path: String, mediaType: String): Long {
        var newId = 0L
        viewModelScope.launch {
            val newFolder = Folder(
                name = name,
                path = path,
                mediaType = mediaType
            )
            newId = repository.createFolder(newFolder)
            loadAllFolders() // Refresh the list
        }
        return newId
    }
    
    fun toggleFolderFavorite(folder: Folder) {
        viewModelScope.launch {
            val newFavoriteState = !folder.isFavorite
            repository.toggleFavorite(folder.id, newFavoriteState)
            
            // Update the folder in the corresponding list
            val allLists = listOf(audioFolders, videoFolders, imageFolders)
            for (list in allLists) {
                val index = list.indexOfFirst { it.id == folder.id }
                if (index != -1) {
                    val updated = folder.copy(isFavorite = newFavoriteState)
                    list[index] = updated
                }
            }
            
            // Update favorite folders list
            loadFavoriteFolders()
        }
    }
    
    fun deleteFolder(id: Long) {
        viewModelScope.launch {
            repository.deleteFolder(id)
            loadAllFolders() // Refresh the list
        }
    }
}

// Extension function to clear and add all items to observable list
fun <T> mutableStateListOf<T>.clearAndAddAll(items: List<T>) {
    this.clear()
    this.addAll(items)
}