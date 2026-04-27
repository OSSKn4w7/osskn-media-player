package com.osskn.mediaplayer.ui.screens.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.osskn.mediaplayer.R
import com.osskn.mediaplayer.model.MediaFile
import com.osskn.mediaplayer.ui.components.MiniPlayer
import com.osskn.mediaplayer.ui.components.MusicListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    navController: NavController,
    musicFiles: List<MediaFile> = emptyList(),
    isLoading: Boolean = false,
    currentPlaying: MediaFile? = null,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onPlayClick: (MediaFile) -> Unit = {},
    onFavoriteClick: (MediaFile) -> Unit = {},
    onMiniPlayerClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_music)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            MiniPlayer(
                mediaFile = currentPlaying,
                isPlaying = isPlaying,
                progress = progress,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onPreviousClick = onPreviousClick,
                onClick = onMiniPlayerClick
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (musicFiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(musicFiles, key = { it.id }) { mediaFile ->
                    MusicListItem(
                        mediaFile = mediaFile,
                        isPlaying = currentPlaying?.id == mediaFile.id && isPlaying,
                        onPlayClick = { onPlayClick(mediaFile) },
                        onFavoriteClick = { onFavoriteClick(mediaFile) }
                    )
                }
            }
        }
    }
}
