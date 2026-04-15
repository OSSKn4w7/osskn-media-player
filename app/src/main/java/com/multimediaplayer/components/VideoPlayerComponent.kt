package com.multimediaplayer.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.multimediaplayer.services.MediaPlayerService
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoUri: Uri,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var player: ExoPlayer? by remember { mutableStateOf(null) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isControlsVisible by remember { mutableStateOf(true) }
    
    // Create and manage player lifecycle
    DisposableEffect(lifecycleOwner) {
        val newPlayer = ExoPlayer.Builder(context).build()
        player = newPlayer
        
        val mediaItem = MediaItem.fromUri(videoUri)
        newPlayer.setMediaItem(mediaItem)
        newPlayer.prepare()
        
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    isPlayerReady = true
                }
            }
            
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        
        newPlayer.addListener(listener)
        
        onDispose {
            newPlayer.removeListener(listener)
            newPlayer.release()
            player = null
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Video player view
        player?.let { exoPlayer ->
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        setPlayer(exoPlayer)
                        useController = false // We'll implement our own controls
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Custom video controls overlay
        if (isControlsVisible) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top controls (back button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        )
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(50))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
                
                // Center controls (play/pause)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                if (player?.isPlaying == true) {
                                    player?.pause()
                                } else {
                                    player?.play()
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(50))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
                
                // Bottom controls (progress, play/pause, volume)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Progress slider (simplified - would need more implementation)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(player?.currentPosition ?: 0L),
                                color = Color.White
                            )
                            
                            Slider(
                                value = ((player?.currentPosition ?: 0L).toFloat() / 
                                        (player?.duration?.toFloat() ?: 1f)).coerceIn(0f, 1f),
                                onValueChange = { progress ->
                                    player?.seekTo((progress * (player?.duration ?: 0L)).toLong())
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                            
                            Text(
                                text = formatTime(player?.duration ?: 0L),
                                color = Color.White
                            )
                        }
                        
                        // Secondary controls row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = { /* Toggle mute */ }) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Volume",
                                    tint = Color.White
                                )
                            }
                            
                            IconButton(onClick = { /* Toggle fullscreen */ }) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = Color.White
                                )
                            }
                            
                            IconButton(onClick = { /* Toggle playback speed */ }) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Speed",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Click area to toggle controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isControlsVisible = !isControlsVisible }
        )
    }
}

// Helper function to format time in MM:SS or HH:MM:SS
fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = (milliseconds / (1000 * 60 * 60))
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}