package com.osskn.mediaplayer.ui.components

import android.view.View
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.osskn.mediaplayer.model.MediaFile

@Composable
fun VideoPlayerScreen(
    mediaFile: MediaFile?,
    exoPlayer: androidx.media3.common.Player?,
    onBackClick: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var isFastPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = {
                        if (exoPlayer?.isPlaying == true) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer?.play()
                        }
                    },
                    onPress = {
                        showControls = true
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { },
                    onDragEnd = { },
                    onDragCancel = { },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // 快进/快退逻辑
                        exoPlayer?.let { player ->
                            val currentPosition = player.currentPosition
                            val seekTo = currentPosition + (dragAmount * 100).toLong()
                            player.seekTo(seekTo.coerceAtLeast(0))
                        }
                    }
                )
            }
    ) {
        // 视频播放器
        exoPlayer?.let { player ->
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_FULLSCREEN
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 控制层
        if (showControls) {
            VideoControls(
                mediaFile = mediaFile,
                isFastPlaying = isFastPlaying,
                onBackClick = onBackClick,
                onFastPlayStart = {
                    isFastPlaying = true
                    exoPlayer?.setPlaybackSpeed(2.0f)
                },
                onFastPlayEnd = {
                    isFastPlaying = false
                    exoPlayer?.setPlaybackSpeed(1.0f)
                }
            )
        }
    }
}

@Composable
private fun VideoControls(
    mediaFile: MediaFile?,
    isFastPlaying: Boolean,
    onBackClick: () -> Unit,
    onFastPlayStart: () -> Unit,
    onFastPlayEnd: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 返回按钮
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
        }

        // 标题
        mediaFile?.let {
            Text(
                text = it.title,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
        }

        // 长按右半屏倍速提示
        if (isFastPlaying) {
            Text(
                text = "2x",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }

        // 右半屏长按区域（倍速）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onFastPlayStart()
                            tryAwaitRelease()
                            onFastPlayEnd()
                        }
                    )
                }
        )
    }
}
