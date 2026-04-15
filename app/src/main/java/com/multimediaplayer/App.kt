package com.multimediaplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multimediaplayer.screens.*
import com.multimediaplayer.ui.viewmodels.MediaViewModel

@Composable
fun MultimediaApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("home") { HomeScreen(navController) }
        composable("music") { MusicPlayerScreen(navController = navController) }
        composable("video") { VideoScreen(navController = navController) }
        composable("images") { ImageScreen(navController = navController) }
        composable("folders") { FolderManagementScreen(navController = navController) }
        composable("share") { ShareScreen(navController = navController) }
        composable("github") { GitHubIntegrationScreen(navController = navController) }
        composable("video_player/{videoPath}") { backStackEntry ->
            val videoPath = backStackEntry.arguments?.getString("videoPath") ?: ""
            FullVideoPlayerScreen(videoPath, navController)
        }
        composable("image_viewer/{imagePath}") { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
            FullImageViewerScreen(imagePath, navController)
        }
    }
}