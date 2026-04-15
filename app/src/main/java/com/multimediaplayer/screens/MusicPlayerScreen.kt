package com.multimediaplayer.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.components.AudioPlayerBottomSheet
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    viewModel: MediaViewModel = viewModel(),
    navController: NavController? = null
) {
    val showPlayerBottomSheet = remember { mutableStateOf(false) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadMusic()
    }
    
    val scaffoldState = rememberBottomSheetScaffoldState()
    
    BottomSheetScaffold(
        sheetContent = {
            if (showPlayerBottomSheet.value && selectedMediaUri != null) {
                AudioPlayerBottomSheet(
                    selectedMediaUri = selectedMediaUri,
                    onDismiss = { showPlayerBottomSheet.value = false }
                )
            }
        },
        sheetPeekHeight = 0.dp, // Start hidden
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Music Player") },
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
            items(viewModel.musicItems) { song ->
                SongItemWithPlayback(
                    song = song,
                    onPlayClick = { mediaItem ->
                        selectedMediaUri = Uri.parse(mediaItem.path)
                        showPlayerBottomSheet.value = true
                    },
                    onFavoriteChange = { item -> 
                        viewModel.toggleFavorite(item) 
                    }
                )
            }
        }
    }
}

@Composable
fun SongItemWithPlayback(
    song: MediaItem,
    onPlayClick: (MediaItem) -> Unit = {},
    onFavoriteChange: (MediaItem) -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(song.title) },
        supportingContent = { 
            Text("${song.duration / 60000}:${(song.duration % 60000 / 1000).toString().padStart(2, '0')} • ${song.path}") 
        },
        trailingContent = {
            Row {
                IconButton(onClick = { onFavoriteChange(song) }) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (song.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (song.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { onPlayClick(song) }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Play")
                }
            }
        }
    )
}