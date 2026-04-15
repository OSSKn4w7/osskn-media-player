package com.multimediaplayer.data.local

import androidx.room.*
import com.multimediaplayer.models.MediaItem

@Dao
interface MediaDao {
    @Query("SELECT * FROM mediaitem WHERE type = :type ORDER BY dateAdded DESC")
    suspend fun getMediaByType(type: String): List<MediaItem>
    
    @Query("SELECT * FROM mediaitem ORDER BY dateAdded DESC LIMIT :count")
    suspend fun getRecentMedia(count: Int): List<MediaItem>
    
    @Query("SELECT * FROM mediaitem WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    suspend fun getFavoriteMedia(): List<MediaItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(vararg mediaItem: MediaItem)
    
    @Update
    suspend fun updateMedia(mediaItem: MediaItem)
    
    @Delete
    suspend fun deleteMedia(mediaItem: MediaItem)
    
    @Query("UPDATE mediaitem SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
    
    @Query("DELETE FROM mediaitem WHERE id = :id")
    suspend fun deleteMediaById(id: Long)
}