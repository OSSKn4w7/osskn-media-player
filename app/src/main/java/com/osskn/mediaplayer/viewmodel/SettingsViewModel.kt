package com.osskn.mediaplayer.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    private val _backgroundPlay = MutableStateFlow(true)
    val backgroundPlay: StateFlow<Boolean> = _backgroundPlay.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _autoUpload = MutableStateFlow(false)
    val autoUpload: StateFlow<Boolean> = _autoUpload.asStateFlow()

    private val _githubToken = MutableStateFlow<String?>(null)
    val githubToken: StateFlow<String?> = _githubToken.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _backgroundPlay.value = preferences[BACKGROUND_PLAY_KEY] ?: true
                _darkMode.value = preferences[DARK_MODE_KEY] ?: false
                _autoUpload.value = preferences[AUTO_UPLOAD_KEY] ?: false
                _githubToken.value = preferences[GITHUB_TOKEN_KEY]
            }
        }
    }

    fun setBackgroundPlay(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[BACKGROUND_PLAY_KEY] = enabled
            }
            _backgroundPlay.value = enabled
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DARK_MODE_KEY] = enabled
            }
            _darkMode.value = enabled
        }
    }

    fun setAutoUpload(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[AUTO_UPLOAD_KEY] = enabled
            }
            _autoUpload.value = enabled
        }
    }

    fun setGithubToken(token: String?) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                if (token != null) {
                    preferences[GITHUB_TOKEN_KEY] = token
                } else {
                    preferences.remove(GITHUB_TOKEN_KEY)
                }
            }
            _githubToken.value = token
        }
    }

    companion object {
        private val BACKGROUND_PLAY_KEY = booleanPreferencesKey("background_play")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val AUTO_UPLOAD_KEY = booleanPreferencesKey("auto_upload")
        private val GITHUB_TOKEN_KEY = stringPreferencesKey("github_token")
    }
}
