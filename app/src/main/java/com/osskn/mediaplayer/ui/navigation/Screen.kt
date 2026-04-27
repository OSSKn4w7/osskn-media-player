package com.osskn.mediaplayer.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Music : Screen("music")
    object Video : Screen("video")
    object Images : Screen("images")
    object Files : Screen("files")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object MusicPlayer : Screen("music_player/{mediaId}") {
        fun createRoute(mediaId: String) = "music_player/$mediaId"
    }
    object VideoPlayer : Screen("video_player/{mediaId}") {
        fun createRoute(mediaId: String) = "video_player/$mediaId"
    }
    object ImageViewer : Screen("image_viewer/{mediaId}") {
        fun createRoute(mediaId: String) = "image_viewer/$mediaId"
    }
}
