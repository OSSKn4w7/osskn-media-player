package com.multimediaplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.multimediaplayer.ui.viewmodels.GitHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubIntegrationScreen(
    navController: NavController? = null,
    viewModel: GitHubViewModel = viewModel()
) {
    var tokenInput by remember { mutableStateOf("") }
    var repoName by remember { mutableStateOf("multimedia-player-cloud") }
    var showTokenInput by remember { mutableStateOf(!viewModel.isAuthenticated.value) }
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Cloud Sync") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Authentication section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (viewModel.isAuthenticated.value) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (viewModel.isAuthenticated.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (viewModel.isAuthenticated.value) "Connected to GitHub" else "Connect to GitHub",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (!viewModel.isAuthenticated.value) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Enter your GitHub personal access token:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            label = { Text("Personal Access Token") },
                            placeholder = { Text("Enter your GitHub token...") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { 
                                if (tokenInput.isNotBlank()) {
                                    viewModel.authenticateWithToken(tokenInput)
                                }
                            },
                            enabled = tokenInput.isNotBlank() && !viewModel.isSyncing.value,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Authenticate")
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Connected! Repository is ready for sync.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.logout() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
            
            if (viewModel.isAuthenticated.value) {
                // Repository setup section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Cloud Repository",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextField(
                            value = repoName,
                            onValueChange = { repoName = it },
                            label = { Text("Repository Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.setupRepository(repoName) },
                            enabled = !viewModel.isSyncing.value,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Setup Repository")
                        }
                    }
                }
                
                // Sync section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Sync Options",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.syncToCloud() },
                            enabled = !viewModel.isSyncing.value,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sync Media to Cloud")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.downloadFromCloud() },
                            enabled = !viewModel.isSyncing.value,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Download from Cloud")
                        }
                    }
                }
            }
            
            // Status section
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Status",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (viewModel.isSyncing.value) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Processing...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = viewModel.lastSyncMessage.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}