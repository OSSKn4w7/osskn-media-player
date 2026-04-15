package com.multimediaplayer.models

data class MediaItem(
    val id: Long = 0,
    val title: String,
    val path: String,
    val type: String, // "audio", "video", "image"
    val duration: Long = 0, // in milliseconds, for audio/video
    val size: Long = 0, // file size in bytes
    val dateAdded: Long = System.currentTimeMillis(), // timestamp
    val isFavorite: Boolean = false
)