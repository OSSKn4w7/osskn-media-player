package com.multimediaplayer.data.repository

import com.multimediaplayer.data.local.FolderDao
import com.multimediaplayer.data.local.FolderEntity
import com.multimediaplayer.data.local.toFolder
import com.multimediaplayer.data.local.toFolderEntity
import com.multimediaplayer.data.models.Folder

class FolderRepository(
    private val folderDao: FolderDao
) {
    suspend fun getAllFolders(): List<Folder> {
        return folderDao.getAllFolders().map { it.toFolder() }
    }
    
    suspend fun getFoldersByType(type: String): List<Folder> {
        return folderDao.getFoldersByType(type).map { it.toFolder() }
    }
    
    suspend fun getFavoriteFolders(): List<Folder> {
        return folderDao.getFavoriteFolders().map { it.toFolder() }
    }
    
    suspend fun getFolderById(id: Long): Folder? {
        return folderDao.getFolderById(id)?.toFolder()
    }
    
    suspend fun createFolder(folder: Folder): Long {
        val folderEntity = folder.toFolderEntity()
        return folderDao.insertFolder(folderEntity)
    }
    
    suspend fun updateFolder(folder: Folder) {
        val folderEntity = folder.toFolderEntity()
        folderDao.updateFolder(folderEntity)
    }
    
    suspend fun deleteFolder(id: Long) {
        folderDao.deleteFolderById(id)
    }
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        folderDao.updateFavorite(id, isFavorite)
    }
}