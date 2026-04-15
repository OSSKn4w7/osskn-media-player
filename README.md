# osskn播放器

A comprehensive multimedia player application built with Jetpack Compose for Android 13-16, supporting audio, video, and image playback with advanced features.

## Features

### Core Playback
- Audio player with playback controls, playlists, and equalizer
- Video player with gesture controls, playback speed, and fullscreen support
- Image viewer with zoom, pan, and rotate functionality

### Media Management
- Automatic scanning of device media files
- Categorization by media type (audio, video, images)
- Favorites system for quick access
- Custom folder creation and management

### Sharing & Cloud Sync
- Receive shared content from other apps
- Share media files with other applications
- GitHub-based cloud storage for backup and sync
- Secure token-based authentication

### Technical Features
- Built with Jetpack Compose for modern UI
- Media3 ExoPlayer for robust playback
- Room database for local persistence
- MVVM architecture for clean code separation
- Coil for efficient image loading
- EncryptedSharedPreferences for secure credential storage

## Architecture

The app follows a clean architecture with:

- **UI Layer**: Compose-based screens with ViewModel state management
- **Data Layer**: Repository pattern with Room database and network APIs
- **Domain Layer**: Business logic and use cases

## Permissions

- Storage access for media files
- Internet access for cloud sync
- Network state access
- Camera permission (optional)

## Getting Started

1. Clone the repository
2. Open in Android Studio Hedgehog or later
3. Build and run on Android 13+ devices

## Dependencies

- Kotlin and Jetpack Compose
- Media3 (ExoPlayer)
- Room Database
- Retrofit for networking
- Coil for image loading
- OkHttp with logging interceptor
- Androidx Security Crypto

## Screenshots

The app includes:
- Home screen with quick access cards
- Dedicated music, video, and image galleries
- Fullscreen playback interfaces
- Folder management screen
- Share and sync functionality
- GitHub integration screen