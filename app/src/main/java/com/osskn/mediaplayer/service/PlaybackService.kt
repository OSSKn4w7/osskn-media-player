package com.osskn.mediaplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, createPlayer())
            .setSessionActivity(createPendingIntent())
            .build()
    }

    private fun createPlayer(): Player {
        return androidx.media3.exoplayer.ExoPlayer.Builder(this)
            .build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, Class.forName("com.osskn.mediaplayer.MainActivity"))
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
