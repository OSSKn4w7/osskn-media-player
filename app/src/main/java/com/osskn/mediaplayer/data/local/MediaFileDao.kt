package com.osskn.mediaplayer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.osskn.mediaplayer.model.MediaFile
import com.osskn.mediaplayer.model.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaFileDao {
    @Query("SELECT * FROM media_files WHERE mediaType = :type ORDER BY title ASC")
    fun getMediaByType(type: MediaType): Flow<List<MediaFile>>

    @Query("SELECT * FROM media_files WHERE isFavorite = 1 ORDER BY dateModified DESC")
    fun getFavorites(): Flow<List<MediaFile>>

    @Query("SELECT * FROM media_files WHERE id = :id")
    suspend fun getMediaById(id: Long): MediaFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaFiles: List<MediaFile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaFile: MediaFile)

    @Update
    suspend fun update(mediaFile: MediaFile)

    @Delete
    suspend fun delete(mediaFile: MediaFile)

    @Query("UPDATE media_files SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM media_files")
    suspend fun deleteAll()
}
