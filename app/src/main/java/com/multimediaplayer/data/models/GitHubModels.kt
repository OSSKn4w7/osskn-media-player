package com.multimediaplayer.data.models

data class GitHubRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: GitHubUser,
    val private: Boolean,
    val url: String,
    val createdAt: String,
    val updatedAt: String
)

data class GitHubUser(
    val login: String,
    val id: Long,
    val avatarUrl: String?,
    val name: String?,
    val bio: String?
)

data class GitHubFile(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    val htmlUrl: String
)

data class GitHubCommit(
    val sha: String,
    val message: String,
    val author: GitHubUser,
    val date: String
)

data class GitHubAuthResponse(
    val token: String
)

data class GitHubCreateRepoRequest(
    val name: String,
    val `private`: Boolean = true,
    val description: String = "Media files stored from Multimedia Player app"
)

data class GitHubUploadFileRequest(
    val message: String,
    val content: String,  // Base64 encoded content
    val branch: String = "main"
)