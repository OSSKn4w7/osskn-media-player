package com.multimediaplayer.data.api

import com.multimediaplayer.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {
    @GET("/user")
    suspend fun getUserInfo(@Header("Authorization") token: String): Response<GitHubUser>
    
    @POST("/user/repos")
    suspend fun createRepo(
        @Header("Authorization") token: String,
        @Body repoRequest: GitHubCreateRepoRequest
    ): Response<GitHubRepo>
    
    @GET("/user/repos")
    suspend fun getUserRepos(@Header("Authorization") token: String): Response<List<GitHubRepo>>
    
    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContents(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Response<GitHubFile>
    
    @PUT("/repos/{owner}/{repo}/contents/{path}")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body uploadRequest: GitHubUploadFileRequest
    ): Response<Any>
    
    @GET("/repos/{owner}/{repo}/contents/")
    suspend fun getRepoContents(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<List<GitHubFile>>
    
    @DELETE("/repos/{owner}/{repo}/contents/{path}")
    suspend fun deleteFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body request: Map<String, String> // Contains message and sha
    ): Response<Any>
}