package com.osskn.mediaplayer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.osskn.mediaplayer.model.CustomFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFolderDao {
    @Query("SELECT * FROM custom_folders ORDER BY dateCreated DESC")
    fun getAllFolders(): Flow<List<CustomFolder>>

    @Query("SELECT * FROM custom_folders WHERE id = :id")
    suspend fun getFolderById(id: Long): CustomFolder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: CustomFolder)

    @Update
    suspend fun updateFolder(folder: CustomFolder)

    @Delete
    suspend fun deleteFolder(folder: CustomFolder)
}
