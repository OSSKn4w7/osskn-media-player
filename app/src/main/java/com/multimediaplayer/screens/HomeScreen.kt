package com.multimediaplayer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.models.MediaItem
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MediaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // Load initial data when screen is first shown
        viewModel.loadMusic()
        viewModel.loadVideos()
        viewModel.loadImages()
        viewModel.loadFavorites()
    }
    
    val allMediaItems = viewModel.musicItems + viewModel.videoItems + viewModel.imageItems
    val sortedMediaItems = allMediaItems.sortedByDescending { it.dateAdded }.take(5)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multimedia Player") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Quick access buttons for different media types
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MediaButton(
                    onClick = { 
                        viewModel.loadMusic() // Trigger refresh
                        navController.navigate("music") 
                    },
                    icon = Icons.Default.MusicNote,
                    label = "Music",
                    color = MaterialTheme.colorScheme.primary
                )
                
                MediaButton(
                    onClick = { 
                        viewModel.loadVideos() // Trigger refresh
                        navController.navigate("video") 
                    },
                    icon = Icons.Default.Movie,
                    label = "Videos",
                    color = MaterialTheme.colorScheme.secondary
                )
                
                MediaButton(
                    onClick = { 
                        viewModel.loadImages() // Trigger refresh
                        navController.navigate("images") 
                    },
                    icon = Icons.Default.Image,
                    label = "Images",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // Folders button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { navController.navigate("folders") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = "Folders",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Manage Folders",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Open"
                    )
                }
            }
            
            // Share button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { navController.navigate("share") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Share Media",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Open"
                    )
                }
            }
            
            // GitHub sync button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { navController.navigate("github") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = "GitHub Sync",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Cloud Sync (GitHub)",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Open"
                    )
                }
            }
            
            // Recently added section
            Text(
                text = "Recently Added",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(sortedMediaItems) { item ->
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text(item.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                // Handle item click based on type
                                when (item.type) {
                                    "audio" -> navController.navigate("music")
                                    "video" -> navController.navigate("video")
                                    "image" -> navController.navigate("images")
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )
        }
    }
}