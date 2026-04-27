package com.osskn.mediaplayer.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = MediaFile::class,
            parentColumns = ["id"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mediaId")]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mediaId: Long,
    val dateAdded: Long = System.currentTimeMillis()
)
