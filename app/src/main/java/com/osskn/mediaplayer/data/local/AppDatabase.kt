package com.osskn.mediaplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.osskn.mediaplayer.model.CustomFolder
import com.osskn.mediaplayer.model.Favorite
import com.osskn.mediaplayer.model.MediaFile

@Database(
    entities = [MediaFile::class, Favorite::class, CustomFolder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaFileDao(): MediaFileDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun customFolderDao(): CustomFolderDao

    companion object {
        const val DATABASE_NAME = "osskn_media_player.db"
    }
}
