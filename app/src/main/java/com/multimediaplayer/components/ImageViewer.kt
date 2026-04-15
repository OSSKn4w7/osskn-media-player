package com.multimediaplayer.components

import android.net.Uri
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.multimediaplayer.models.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    imageUri: Uri,
    navController: NavController? = null,
    mediaItem: MediaItem? = null,
    onFavoriteToggle: ((MediaItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offsetX += panChange.x
        offsetY += panChange.y
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = imageUri.toString(),
            contentDescription = mediaItem?.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
        
        // Top app bar with controls
        TopAppBar(
            title = { Text(mediaItem?.title ?: "Image Viewer") },
            navigationIcon = {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Favorite button
                mediaItem?.let { item ->
                    IconButton(onClick = { 
                        onFavoriteToggle?.invoke(item) 
                    }) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (item.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Share button
                IconButton(onClick = { /* Share image */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
                
                // More options
                IconButton(onClick = { /* Show options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            )
        )
        
        // Bottom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { 
                scale = 1f
                rotation = 0f
                offsetX = 0f
                offsetY = 0f
            }) {
                Icon(
                    imageVector = Icons.Default.ZoomOutMap,
                    contentDescription = "Reset view"
                )
            }
            
            IconButton(onClick = { rotation = (rotation + 90) % 360 }) {
                Icon(
                    imageVector = Icons.Default.RotateRight,
                    contentDescription = "Rotate"
                )
            }
            
            IconButton(onClick = { /* Save image */ }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download"
                )
            }
        }
    }
}