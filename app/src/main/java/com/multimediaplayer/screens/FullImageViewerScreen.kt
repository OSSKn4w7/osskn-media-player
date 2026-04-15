package com.multimediaplayer.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.components.ImageViewerScreen
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@Composable
fun FullImageViewerScreen(
    imagePath: String,
    navController: NavController? = null,
    viewModel: MediaViewModel = viewModel()
) {
    val imageUri = Uri.parse(imagePath)
    
    // Find the media item to pass to the viewer
    val mediaItem = viewModel.imageItems.find { it.path == imagePath }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ImageViewerScreen(
            imageUri = imageUri,
            navController = navController,
            mediaItem = mediaItem,
            onFavoriteToggle = { item ->
                viewModel.toggleFavorite(item)
            }
        )
    }
}