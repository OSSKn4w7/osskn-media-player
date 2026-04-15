package com.multimediaplayer.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.utils.Converters
import com.multimediaplayer.Application

@Database(
    entities = [MediaItem::class, FolderEntity::class],
    version = 2,  // Incremented version since we added a new entity
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun folderDao(): FolderDao
    
    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null
        
        fun getDatabase(application: Application): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application,
                    MediaDatabase::class.java,
                    "media_database"
                )
                .fallbackToDestructiveMigration() // Recreate database if schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}