package com.multimediaplayer.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    navController: NavController? = null,
    viewModel: MediaViewModel = viewModel()
) {
    val context = LocalContext.current
    var expandedItem by remember { mutableStateOf<Long?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadMusic()
        viewModel.loadVideos()
        viewModel.loadImages()
    }
    
    val allMedia = viewModel.musicItems + viewModel.videoItems + viewModel.imageItems
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Media") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
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
            item {
                Text(
                    text = "Your Media Files",
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            items(allMedia) { mediaItem ->
                MediaItemForSharing(
                    mediaItem = mediaItem,
                    isExpanded = expandedItem == mediaItem.id,
                    onExpandToggle = { 
                        expandedItem = if (expandedItem == mediaItem.id) null else mediaItem.id 
                    },
                    onShareClick = { item ->
                        shareMedia(context, item)
                    }
                )
            }
            
            if (allMedia.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No media files found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaItemForSharing(
    mediaItem: MediaItem,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onShareClick: (MediaItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (mediaItem.type) {
                        "audio" -> Icons.Default.MusicNote
                        "video" -> Icons.Default.Movie
                        "image" -> Icons.Default.Image
                        else -> Icons.Default.Help
                    },
                    contentDescription = null,
                    tint = when (mediaItem.type) {
                        "audio" -> MaterialTheme.colorScheme.primary
                        "video" -> MaterialTheme.colorScheme.secondary
                        "image" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mediaItem.title,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${mediaItem.type.capitalized()} • ${formatFileSize(mediaItem.size)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onExpandToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
            
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onShareClick(mediaItem) }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextButton(
                        onClick = { /* TODO: Implement copy to clipboard */ }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Path")
                    }
                }
            }
        }
    }
}

private fun String.capitalized(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun formatFileSize(sizeInBytes: Long): String {
    return when {
        sizeInBytes < 1024 -> "$sizeInBytes B"
        sizeInBytes < 1024 * 1024 -> "${(sizeInBytes / 1024.0).format(2)} KB"
        sizeInBytes < 1024 * 1024 * 1024 -> "${(sizeInBytes / (1024.0 * 1024.0)).format(2)} MB"
        else -> "${(sizeInBytes / (1024.0 * 1024.0 * 1024.0)).format(2)} GB"
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

private fun shareMedia(context: android.content.Context, mediaItem: MediaItem) {
    val fileUri = Uri.parse(mediaItem.path)
    
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = when (mediaItem.type) {
            "audio" -> "audio/*"
            "video" -> "video/*"
            "image" -> "image/*"
            else -> "*/*"
        }
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    
    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    context.startActivity(chooserIntent)
}