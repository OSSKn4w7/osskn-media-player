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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multimediaplayer.data.models.Folder
import com.multimediaplayer.ui.viewmodels.FolderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderManagementScreen(
    navController: NavController? = null,
    viewModel: FolderViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllFolders()
    }
    
    var showDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    var folderPath by remember { mutableStateOf("") }
    var selectedMediaType by remember { mutableStateOf("mixed") }
    
    if (showDialog) {
        CreateFolderDialog(
            folderName = folderName,
            onFolderNameChange = { folderName = it },
            folderPath = folderPath,
            onFolderPathChange = { folderPath = it },
            selectedMediaType = selectedMediaType,
            onMediaTypeChange = { selectedMediaType = it },
            onConfirm = {
                if (folderName.isNotEmpty() && folderPath.isNotEmpty()) {
                    viewModel.createNewFolder(folderName, folderPath, selectedMediaType)
                    showDialog = false
                    folderName = ""
                    folderPath = ""
                }
            },
            onDismiss = { showDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folders") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Folder")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Folders by type
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "All Folders",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(viewModel.folders) { folder ->
                    FolderItem(
                        folder = folder,
                        onFolderClick = { /* Navigate to folder content */ },
                        onFavoriteToggle = { viewModel.toggleFolderFavorite(folder) },
                        onDelete = { viewModel.deleteFolder(folder.id) }
                    )
                }
                
                if (viewModel.folders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No folders created yet",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(
    folderName: String,
    onFolderNameChange: (String) -> Unit,
    folderPath: String,
    onFolderPathChange: (String) -> Unit,
    selectedMediaType: String,
    onMediaTypeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Folder") },
        text = {
            Column {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = onFolderNameChange,
                    label = { Text("Folder Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = folderPath,
                    onValueChange = onFolderPathChange,
                    label = { Text("Folder Path") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Media Type:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                
                MediaTypeSelection(
                    selectedType = selectedMediaType,
                    onTypeSelected = onMediaTypeChange
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MediaTypeSelection(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val mediaTypes = listOf(
        "mixed" to "Mixed Content",
        "audio" to "Audio Only",
        "video" to "Video Only",
        "image" to "Image Only"
    )
    
    Column {
        mediaTypes.forEach { (type, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) }
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FolderItem(
    folder: Folder,
    onFolderClick: (Folder) -> Unit,
    onFavoriteToggle: (Folder) -> Unit,
    onDelete: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onFolderClick(folder) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (folder.mediaType) {
                    "audio" -> Icons.Default.MusicNote
                    "video" -> Icons.Default.Movie
                    "image" -> Icons.Default.Image
                    else -> Icons.Default.Folder
                },
                contentDescription = null,
                tint = when (folder.mediaType) {
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
                    text = folder.name,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${folder.itemCount} items • ${folder.path}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { onFavoriteToggle(folder) }) {
                Icon(
                    imageVector = if (folder.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (folder.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (folder.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { onDelete(folder.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete folder",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}