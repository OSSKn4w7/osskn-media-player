package com.multimediaplayer.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.multimediaplayer.services.MediaPlayerService
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerBottomSheet(
    selectedMediaUri: Uri?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var playerService by remember { mutableStateOf<MediaPlayerService?>(null) }
    
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    
    // Start/Bind MediaPlayerService
    LaunchedEffect(selectedMediaUri) {
        selectedMediaUri?.let { uri ->
            // Attempt to connect to MediaPlayerService
            // (Actual service connection code would go here)
        }
    }
    
    // Simulate audio controls with a mock player state
    var mockProgress by remember { mutableStateOf(0f) }
    var mockIsPlaying by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(targetValue = mockProgress, label = "progress animation")
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Draggable handle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Divider(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp),
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Media info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(3f)
            ) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = selectedMediaUri?.lastPathSegment ?: "No media selected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
        
        // Progress indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime((animatedProgress * duration).toLong()),
                style = MaterialTheme.typography.bodySmall
            )
            
            Slider(
                value = animatedProgress,
                onValueChange = { 
                    mockProgress = it 
                    // In a real implementation, we would seek to the position
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Player controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Previous track */ }) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            IconButton(
                onClick = { 
                    mockIsPlaying = !mockIsPlaying 
                    if (mockIsPlaying) {
                        // In a real implementation, start progress simulation
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    if (mockIsPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (mockIsPlaying) "Pause" else "Play",
                    modifier = Modifier.size(48.dp)
                )
            }
            
            IconButton(onClick = { /* Next track */ }) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Additional controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Shuffle */ }) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Shuffle"
                )
            }
            
            IconButton(onClick = { /* Repeat */ }) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repeat"
                )
            }
            
            IconButton(onClick = { /* Volume */ }) {
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Volume"
                )
            }
            
            IconButton(onClick = { /* Queue */ }) {
                Icon(
                    Icons.Default.QueueMusic,
                    contentDescription = "Queue"
                )
            }
        }
    }
}

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