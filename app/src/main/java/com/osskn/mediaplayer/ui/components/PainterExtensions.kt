package com.osskn.mediaplayer.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.osskn.mediaplayer.R

@Composable
fun ImageVector.toPainter(): Painter {
    return rememberVectorPainter(this)
}

@Composable
fun getPlaceholderPainter(): Painter {
    return painterResource(android.R.drawable.ic_menu_gallery)
}

@Composable
fun getMusicPlaceholderPainter(): Painter {
    return painterResource(android.R.drawable.ic_media_play)
}
