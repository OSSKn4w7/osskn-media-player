package com.multimediaplayer.data.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class GitHubAuthManager(context: Context) {
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "github_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var authToken: String?
        get() = sharedPreferences.getString("auth_token", null)
        set(value) = sharedPreferences.edit().putString("auth_token", value).apply()

    var isLoggedIn: Boolean
        get() = !authToken.isNullOrEmpty()
        set(value) = if (!value) {
            authToken = null
        } else {
            // Do nothing - if setting to true, you'd typically already have a token
        }

    fun logout() {
        authToken = null
    }
}