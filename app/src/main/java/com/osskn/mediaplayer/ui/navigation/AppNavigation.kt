package com.osskn.mediaplayer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.osskn.mediaplayer.R
import com.osskn.mediaplayer.ui.components.FullScreenPlayer
import com.osskn.mediaplayer.ui.components.ImageViewer
import com.osskn.mediaplayer.ui.components.PlayMode
import com.osskn.mediaplayer.ui.components.VideoPlayerScreen
import com.osskn.mediaplayer.ui.screens.files.FilesScreen
import com.osskn.mediaplayer.ui.screens.images.ImagesScreen
import com.osskn.mediaplayer.ui.screens.music.MusicScreen
import com.osskn.mediaplayer.ui.screens.settings.SettingsScreen
import com.osskn.mediaplayer.ui.screens.video.VideoScreen
import com.osskn.mediaplayer.viewmodel.FilesViewModel
import com.osskn.mediaplayer.viewmodel.ImageViewModel
import com.osskn.mediaplayer.viewmodel.MusicViewModel
import com.osskn.mediaplayer.viewmodel.VideoViewModel

data class BottomNavItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelRes: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Music, Icons.Default.MusicNote, R.string.tab_music),
    BottomNavItem(Screen.Video, Icons.Default.Videocam, R.string.tab_video),
    BottomNavItem(Screen.Images, Icons.Default.Image, R.string.tab_images),
    BottomNavItem(Screen.Files, Icons.Default.Folder, R.string.tab_files),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val musicViewModel: MusicViewModel = viewModel()
    val videoViewModel: VideoViewModel = viewModel()
    val imageViewModel: ImageViewModel = viewModel()
    val filesViewModel: FilesViewModel = viewModel()

    val musicFiles by musicViewModel.musicFiles.collectAsState()
    val musicIsLoading by musicViewModel.isLoading.collectAsState()
    val currentPlaying by musicViewModel.currentPlaying.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val progress by musicViewModel.progress.collectAsState()

    val videoFiles by videoViewModel.videoFiles.collectAsState()
    val videoIsLoading by videoViewModel.isLoading.collectAsState()

    val imageFiles by imageViewModel.imageFiles.collectAsState()
    val imageIsLoading by imageViewModel.isLoading.collectAsState()

    val folders by filesViewModel.folders.collectAsState()
    val files by filesViewModel.files.collectAsState()
    val filesIsLoading by filesViewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Music.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Music.route) {
                MusicScreen(
                    navController = navController,
                    musicFiles = musicFiles,
                    isLoading = musicIsLoading,
                    currentPlaying = currentPlaying,
                    isPlaying = isPlaying,
                    progress = progress,
                    onPlayClick = { musicViewModel.playMusic(it) },
                    onFavoriteClick = { musicViewModel.toggleFavorite(it) },
                    onPlayPauseClick = { musicViewModel.togglePlayPause() },
                    onNextClick = { },
                    onPreviousClick = { },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Video.route) {
                VideoScreen(
                    navController = navController,
                    videoFiles = videoFiles,
                    isLoading = videoIsLoading,
                    onVideoClick = { mediaFile ->
                        navController.navigate(Screen.VideoPlayer.createRoute(mediaFile.id.toString()))
                    },
                    onFavoriteClick = { videoViewModel.toggleFavorite(it) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Images.route) {
                ImagesScreen(
                    navController = navController,
                    imageFiles = imageFiles,
                    isLoading = imageIsLoading,
                    onImageClick = { index ->
                        navController.navigate(Screen.ImageViewer.createRoute(index.toString()))
                    },
                    onFavoriteClick = { imageViewModel.toggleFavorite(it) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Files.route) {
                FilesScreen(
                    navController = navController,
                    folders = folders,
                    files = files,
                    isLoading = filesIsLoading,
                    onFolderClick = { folder ->
                        // TODO: 打开文件夹浏览
                    },
                    onFileClick = { mediaFile ->
                        // 根据文件类型跳转到对应播放器
                        when (mediaFile.mediaType) {
                            com.osskn.mediaplayer.model.MediaType.AUDIO -> {
                                musicViewModel.playMusic(mediaFile)
                                navController.navigate(Screen.MusicPlayer.createRoute(mediaFile.id.toString()))
                            }
                            com.osskn.mediaplayer.model.MediaType.VIDEO -> {
                                navController.navigate(Screen.VideoPlayer.createRoute(mediaFile.id.toString()))
                            }
                            com.osskn.mediaplayer.model.MediaType.IMAGE -> {
                                val index = imageFiles.indexOfFirst { it.id == mediaFile.id }
                                if (index >= 0) {
                                    navController.navigate(Screen.ImageViewer.createRoute(index.toString()))
                                }
                            }
                        }
                    },
                    onNewFolderClick = { name, path ->
                        filesViewModel.createFolder(name, path)
                    },
                    onScanClick = { filesViewModel.scanMedia() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }

            composable(
                route = Screen.MusicPlayer.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull()
                val mediaFile = musicFiles.find { it.id == mediaId }
                FullScreenPlayer(
                    mediaFile = mediaFile,
                    isPlaying = isPlaying,
                    progress = progress,
                    currentPosition = 0,
                    duration = mediaFile?.duration ?: 0,
                    playMode = PlayMode.SEQUENCE,
                    onPlayPauseClick = { musicViewModel.togglePlayPause() },
                    onNextClick = { },
                    onPreviousClick = { },
                    onSeekTo = { musicViewModel.seekTo(it) },
                    onPlayModeClick = { },
                    onFavoriteClick = { mediaFile?.let { musicViewModel.toggleFavorite(it) } },
                    onCloseClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull()
                val mediaFile = videoFiles.find { it.id == mediaId }
                VideoPlayerScreen(
                    mediaFile = mediaFile,
                    exoPlayer = null,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ImageViewer.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId")?.toIntOrNull() ?: 0
                val initialIndex = if (imageFiles.isNotEmpty()) mediaId.coerceIn(0, imageFiles.size - 1) else 0
                ImageViewer(
                    images = imageFiles,
                    initialIndex = initialIndex,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
