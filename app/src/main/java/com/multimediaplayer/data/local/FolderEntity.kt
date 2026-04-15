package com.multimediaplayer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.multimediaplayer.data.models.Folder

@Entity(tableName = "folder")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val path: String,
    val mediaType: String, // "audio", "video", "image", or "mixed"
    val itemCount: Int = 0,
    val dateCreated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

fun FolderEntity.toFolder() = Folder(
    id = id,
    name = name,
    path = path,
    mediaType = mediaType,
    itemCount = itemCount,
    dateCreated = dateCreated,
    isFavorite = isFavorite
)

fun Folder.toFolderEntity() = FolderEntity(
    id = id,
    name = name,
    path = path,
    mediaType = mediaType,
    itemCount = itemCount,
    dateCreated = dateCreated,
    isFavorite = isFavorite
)