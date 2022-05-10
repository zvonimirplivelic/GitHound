package com.zvonimirplivelic.githound.remote

import com.zvonimirplivelic.githound.model.GitAuthorResponse
import com.zvonimirplivelic.githound.model.GitRepoDetailResponse
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHoundService {
    @GET("repositories")
    suspend fun getRepositoryList(): Response<GitRepoListResponse>

    @GET("users")
    suspend fun getAuthorDetails(
        @Query("user") user: String
    ): Response<GitAuthorResponse>

    @GET("repos")
    suspend fun getRepositoryDetails(
        @Query("user") user: String,
        @Query("repository") repository: String
    ): Response<GitRepoDetailResponse>

}