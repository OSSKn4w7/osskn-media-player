package com.multimediaplayer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    viewModel: MediaViewModel = viewModel(),
    navController: NavController? = null
) {
    LaunchedEffect(Unit) {
        viewModel.loadVideos()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Videos") },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController?.popBackStack() 
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(viewModel.videoItems) { video ->
                VideoItem(
                    video = video,
                    onFavoriteChange = { item -> 
                        viewModel.toggleFavorite(item) 
                    },
                    onPlayClick = { mediaItem ->
                        // Navigate to full video player screen
                        navController?.navigate("video_player/${mediaItem.path}")
                    }
                )
            }
        }
    }
}

@Composable
fun VideoItem(
    video: MediaItem,
    onFavoriteChange: (MediaItem) -> Unit = {},
    onPlayClick: (MediaItem) -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(video.title) },
        supportingContent = { 
            Text("${video.duration / 60000}:${(video.duration % 60000 / 1000).toString().padStart(2, '0')} • ${video.path}") 
        },
        trailingContent = {
            Row {
                IconButton(onClick = { onFavoriteChange(video) }) {
                    Icon(
                        imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (video.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (video.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { onPlayClick(video) }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Play")
                }
            }
        },
        modifier = Modifier.clickable { onPlayClick(video) }
    )
}