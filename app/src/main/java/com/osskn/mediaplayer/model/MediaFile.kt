package com.osskn.mediaplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_files")
data class MediaFile(
    @PrimaryKey val id: Long,
    val uri: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val size: Long,
    val mimeType: String,
    val mediaType: MediaType,
    val dateAdded: Long,
    val dateModified: Long,
    val filePath: String,
    val isFavorite: Boolean = false,
    val thumbnailUri: String? = null
)

enum class MediaType {
    AUDIO, VIDEO, IMAGE
}
