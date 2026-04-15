package com.multimediaplayer.data.models

data class Folder(
    val id: Long = 0,
    val name: String,
    val path: String,
    val mediaType: String, // "audio", "video", "image", or "mixed"
    val itemCount: Int = 0,
    val dateCreated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)