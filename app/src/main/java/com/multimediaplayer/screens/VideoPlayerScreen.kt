package com.multimediaplayer.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.components.VideoPlayerScreen
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@Composable
fun FullVideoPlayerScreen(
    videoPath: String,
    navController: NavController? = null,
    viewModel: MediaViewModel = viewModel()
) {
    val videoUri = Uri.parse(videoPath)
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPlayerScreen(
            videoUri = videoUri,
            onBack = {
                navController?.popBackStack()
            }
        )
        
        // Overlay for additional controls (favorites, etc.)
        IconButton(
            onClick = {
                // Find the media item and toggle favorite
                val mediaItem = viewModel.videoItems.find { it.path == videoPath }
                if (mediaItem != null) {
                    viewModel.toggleFavorite(mediaItem)
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            val mediaItem = viewModel.videoItems.find { it.path == videoPath }
            Icon(
                imageVector = if (mediaItem?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (mediaItem?.isFavorite == true) "Remove from favorites" else "Add to favorites",
                tint = if (mediaItem?.isFavorite == true) androidx.compose.material3.MaterialTheme.colorScheme.error else androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        }
    }
}