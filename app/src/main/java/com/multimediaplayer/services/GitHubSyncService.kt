package com.multimediaplayer.services

import android.content.Context
import android.util.Base64
import com.multimediaplayer.data.api.GitHubApiService
import com.multimediaplayer.data.managers.GitHubAuthManager
import com.multimediaplayer.data.models.*
import com.multimediaplayer.data.repository.MediaRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

class GitHubSyncService(
    private val context: Context,
    private val mediaRepository: MediaRepository
) {
    private val authManager = GitHubAuthManager(context)
    private val githubApi: GitHubApiService by lazy { createGitHubApi() }

    private fun createGitHubApi(): GitHubApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "MultimediaPlayerApp")

                if (authManager.isLoggedIn && !authManager.authToken.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer ${authManager.authToken}")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApiService::class.java)
    }

    suspend fun authenticate(token: String): Result<GitHubUser> {
        return try {
            val response = githubApi.getUserInfo("Bearer $token")
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    authManager.authToken = token
                    Result.success(user)
                } else {
                    Result.failure(Exception("Authentication failed - invalid response"))
                }
            } else {
                Result.failure(Exception("Authentication failed - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setupPrivateRepo(repoName: String = "multimedia-player-cloud"): Result<GitHubRepo> {
        return try {
            if (!authManager.isLoggedIn) {
                return Result.failure(Exception("Not authenticated"))
            }

            // Try to find existing repo first
            val reposResponse = githubApi.getUserRepos("Bearer ${authManager.authToken!!}")
            if (reposResponse.isSuccessful) {
                val existingRepo = reposResponse.body()?.find { it.name == repoName }
                if (existingRepo != null) {
                    return Result.success(existingRepo)
                }
            }

            // Create a new private repo
            val createRepoRequest = GitHubCreateRepoRequest(
                name = repoName,
                `private` = true,
                description = "Media files backed up from Multimedia Player app"
            )

            val response = githubApi.createRepo("Bearer ${authManager.authToken!!}", createRepoRequest)
            if (response.isSuccessful) {
                val repo = response.body()
                if (repo != null) {
                    Result.success(repo)
                } else {
                    Result.failure(Exception("Failed to create repo - no response body"))
                }
            } else {
                Result.failure(Exception("Failed to create repo - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncToCloud(mediaItemIds: List<Long>? = null): Result<Int> {
        return try {
            if (!authManager.isLoggedIn) {
                return Result.failure(Exception("Not authenticated"))
            }

            // Get all media items or specific ones
            val mediaItems = if (mediaItemIds != null) {
                mediaRepository.getMediaByType("")
                    .filter { it.id in mediaItemIds }
            } else {
                mediaRepository.getMediaByType("")
            }

            var uploadedCount = 0
            val repo = setupPrivateRepo().getOrNull()
                ?: return Result.failure(Exception("Could not access or create repository"))

            // Upload each media item to GitHub
            for (mediaItem in mediaItems) {
                val file = File(mediaItem.path)
                if (file.exists()) {
                    val relativePath = "${mediaItem.type}/${file.name}"
                    
                    // Read file content and encode to base64
                    val content = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
                    
                    val uploadRequest = GitHubUploadFileRequest(
                        message = "Upload ${mediaItem.title} via Multimedia Player",
                        content = content
                    )

                    try {
                        val response = githubApi.uploadFile(
                            token = "Bearer ${authManager.authToken!!}",
                            owner = repo.owner.login,
                            repo = repo.name,
                            path = relativePath,
                            uploadRequest = uploadRequest
                        )
                        
                        if (response.isSuccessful) {
                            uploadedCount++
                        }
                    } catch (e: Exception) {
                        // Continue with other files even if one fails
                        e.printStackTrace()
                    }
                }
            }

            Result.success(uploadedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadFromCloud(): Result<List<GitHubFile>> {
        return try {
            if (!authManager.isLoggedIn) {
                return Result.failure(Exception("Not authenticated"))
            }

            val repo = setupPrivateRepo().getOrNull()
                ?: return Result.failure(Exception("Could not access repository"))

            val response = githubApi.getRepoContents(
                token = "Bearer ${authManager.authToken!!}",
                owner = repo.owner.login,
                repo = repo.name
            )

            if (response.isSuccessful) {
                val files = response.body() ?: emptyList()
                Result.success(files)
            } else {
                Result.failure(Exception("Failed to download from cloud - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        authManager.logout()
    }

    fun isLoggedIn(): Boolean = authManager.isLoggedIn
}