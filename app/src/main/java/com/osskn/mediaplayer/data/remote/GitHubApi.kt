package com.osskn.mediaplayer.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class GitHubUser(
    val login: String,
    val avatar_url: String,
    val name: String?
)

data class GitHubRepo(
    val id: Long,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("html_url") val htmlUrl: String
)

data class CreateRepoRequest(
    val name: String,
    val description: String = "Media backup for osskn媒体播放器",
    @SerializedName("private") val isPrivate: Boolean = true
)

data class FileContent(
    val name: String,
    val path: String,
    val sha: String?,
    val content: String?,
    val encoding: String?
)

data class CreateFileRequest(
    val message: String,
    val content: String,
    val branch: String? = null
)

data class CreateFileResponse(
    val content: FileContent?
)

interface GitHubApi {
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<GitHubUser>

    @POST("user/repos")
    suspend fun createRepository(
        @Header("Authorization") token: String,
        @Body request: CreateRepoRequest
    ): Response<GitHubRepo>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<GitHubRepo>

    @PUT("repos/{owner}/{repo}/contents/{path}")
    @Headers("Content-Type: application/json")
    suspend fun createOrUpdateFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body request: CreateFileRequest
    ): Response<CreateFileResponse>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Response<FileContent>
}
