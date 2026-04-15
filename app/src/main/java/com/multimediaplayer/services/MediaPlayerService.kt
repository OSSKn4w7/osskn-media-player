package com.multimediaplayer.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.multimediaplayer.MainActivity
import com.multimediaplayer.R

class MediaPlayerService : android.app.Service(), Player.Listener {
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val binder = MediaPlayerBinder()

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        player.addListener(this)

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle service start commands
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    fun playMedia(uri: android.net.Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun getDuration(): Long {
        return player.duration
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    // Player.Listener implementations
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        // Update notification based on playback state
        when (playbackState) {
            Player.STATE_ENDED -> stopForeground(STOP_FOREGROUND_REMOVE)
            Player.STATE_READY -> {
                if (player.isPlaying) {
                    startForeground(NOTIFICATION_ID, createNotification(true))
                }
            }
        }
    }

    private fun createNotification(isPlaying: Boolean): Notification {
        val builder = NotificationCompat.Builder(this, "media_playback_channel")
            .setContentTitle("Multimedia Player")
            .setContentText(if (player.isPlaying) "Now Playing" else "Paused")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)

        return builder.build()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}