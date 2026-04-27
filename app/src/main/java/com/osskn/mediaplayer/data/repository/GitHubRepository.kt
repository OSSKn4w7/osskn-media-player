package com.osskn.mediaplayer.data.repository

import android.util.Base64
import com.osskn.mediaplayer.data.remote.CreateFileRequest
import com.osskn.mediaplayer.data.remote.CreateRepoRequest
import com.osskn.mediaplayer.data.remote.GitHubClient
import com.osskn.mediaplayer.data.remote.GitHubUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GitHubRepository {
    private val api = GitHubClient.api
    private var authToken: String? = null

    fun setToken(token: String) {
        authToken = "token $token"
    }

    fun clearToken() {
        authToken = null
    }

    suspend fun getUserInfo(): Result<GitHubUser> = withContext(Dispatchers.IO) {
        try {
            val token = authToken ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))
            val response = api.getUser(token)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get user info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBackupRepository(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val token = authToken ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))
            val request = CreateRepoRequest(
                name = "media-backup",
                description = "Media backup for osskn媒体播放器",
                private = true
            )
            val response = api.createRepository(token, request)
            if (response.isSuccessful || response.code() == 422) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to create repository: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFile(
        owner: String,
        repo: String,
        filePath: String,
        localFile: File
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val token = authToken ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))

            val fileContent = localFile.readBytes()
            val base64Content = Base64.encodeToString(fileContent, Base64.NO_WRAP)

            val request = CreateFileRequest(
                message = "Backup: ${localFile.name}",
                content = base64Content
            )

            val response = api.createOrUpdateFile(
                token = token,
                owner = owner,
                repo = repo,
                path = "media/$filePath",
                request = request
            )

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to upload file: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkRepositoryExists(owner: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val token = authToken ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))
            val response = api.getRepository(token, owner, "media-backup")
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
