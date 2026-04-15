package com.multimediaplayer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(
    viewModel: MediaViewModel = viewModel(),
    navController: NavController? = null
) {
    LaunchedEffect(Unit) {
        viewModel.loadImages()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Images") },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(viewModel.imageItems) { image ->
                ImageItem(
                    image = image,
                    onFavoriteChange = { item -> 
                        viewModel.toggleFavorite(item) 
                    },
                    onClick = { mediaItem ->
                        // Navigate to full image viewer
                        navController?.navigate("image_viewer/${mediaItem.path}")
                    }
                )
            }
        }
    }
}

@Composable
fun ImageItem(
    image: MediaItem,
    onFavoriteChange: (MediaItem) -> Unit = {},
    onClick: (MediaItem) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick(image) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.aspectRatio(1f)
        ) {
            val painter = rememberAsyncImagePainter(model = image.path)
            Image(
                painter = painter,
                contentDescription = image.title,
                modifier = Modifier.fillMaxSize()
            )
            
            IconButton(
                onClick = { onFavoriteChange(image) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (image.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (image.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (image.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}