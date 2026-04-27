package com.osskn.mediaplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_folders")
data class CustomFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val path: String,
    val dateCreated: Long = System.currentTimeMillis()
)
