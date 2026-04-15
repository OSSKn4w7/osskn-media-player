package com.multimediaplayer.data.local

import androidx.room.*
import com.multimediaplayer.data.local.FolderEntity

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder ORDER BY dateCreated DESC")
    suspend fun getAllFolders(): List<FolderEntity>
    
    @Query("SELECT * FROM folder WHERE mediaType = :type ORDER BY dateCreated DESC")
    suspend fun getFoldersByType(type: String): List<FolderEntity>
    
    @Query("SELECT * FROM folder WHERE isFavorite = 1 ORDER BY dateCreated DESC")
    suspend fun getFavoriteFolders(): List<FolderEntity>
    
    @Query("SELECT * FROM folder WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)
    
    @Update
    suspend fun updateFolder(folder: FolderEntity)
    
    @Delete
    suspend fun deleteFolder(folder: FolderEntity)
    
    @Query("UPDATE folder SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
    
    @Query("DELETE FROM folder WHERE id = :id")
    suspend fun deleteFolderById(id: Long)
}