package com.multimediaplayer

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.multimediaplayer.data.repository.MediaRepository
import com.multimediaplayer.services.ShareHandler
import com.multimediaplayer.ui.theme.MultimediaPlayerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle shared content from other apps
        intent?.let { intent ->
            if (intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
                processSharedContent(intent)
            }
        }
        
        setContent {
            MultimediaPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MultimediaApp()
                }
            }
        }
    }
    
    private fun processSharedContent(intent: Intent) {
        lifecycleScope.launch {
            val shareHandler = ShareHandler(
                this@MainActivity,
                MediaRepository(
                    application.getDatabase().mediaDao(),
                    this@MainActivity
                )
            )
            
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    // Handle single file share
                    val sharedFileUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    sharedFileUri?.let { uri ->
                        shareHandler.handleSharedContent(uri)
                    }
                }
                Intent.ACTION_SEND_MULTIPLE -> {
                    // Handle multiple file share
                    val sharedUris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                    sharedUris?.forEach { uri ->
                        shareHandler.handleSharedContent(uri)
                    }
                }
            }
        }
    }
}