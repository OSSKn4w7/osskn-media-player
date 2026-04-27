package com.osskn.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.osskn.mediaplayer.ui.components.PermissionScreen
import com.osskn.mediaplayer.ui.navigation.AppNavigation
import com.osskn.mediaplayer.ui.theme.OssknMediaPlayerTheme
import com.osskn.mediaplayer.utils.PermissionUtils

class MainActivity : ComponentActivity() {
    private var permissionsGranted = false

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.values.all { it }
        if (permissionsGranted) {
            // 权限已授予，显示主界面
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionsGranted = PermissionUtils.hasRequiredPermissions(this)

        if (!permissionsGranted) {
            requestPermissionsLauncher.launch(PermissionUtils.REQUIRED_PERMISSIONS)
        }

        setContent {
            OssknMediaPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (permissionsGranted) {
                        AppNavigation()
                    } else {
                        PermissionScreen(
                            onRequestPermissions = {
                                requestPermissionsLauncher.launch(PermissionUtils.REQUIRED_PERMISSIONS)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 检查权限状态是否改变
        permissionsGranted = PermissionUtils.hasRequiredPermissions(this)
    }
}
