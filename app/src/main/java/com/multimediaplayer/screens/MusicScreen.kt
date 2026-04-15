package com.multimediaplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
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
fun MusicScreen(
    viewModel: MediaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadMusic()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
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
            items(viewModel.musicItems) { song ->
                SongItem(
                    song = song,
                    onFavoriteChange = { item -> 
                        viewModel.toggleFavorite(item) 
                    }
                )
            }
        }
    }
}

@Composable
fun SongItem(
    song: MediaItem,
    onFavoriteChange: (MediaItem) -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(song.title) },
        supportingContent = { Text("${song.duration / 60000}:${(song.duration % 60000 / 1000).toString().padStart(2, '0')}") },
        trailingContent = {
            IconButton(onClick = { onFavoriteChange(song) }) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (song.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (song.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        leadingContent = {
            IconButton(onClick = { /* Play song */ }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
        }
    )
}