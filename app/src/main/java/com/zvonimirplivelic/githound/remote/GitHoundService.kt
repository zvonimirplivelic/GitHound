package com.zvonimirplivelic.githound.remote

import com.zvonimirplivelic.githound.model.GitAuthorResponse
import com.zvonimirplivelic.githound.model.GitRepoDetailResponse
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHoundService {
    @GET("repositories")
    suspend fun getRepositoryList(): Response<GitRepoListResponse>

    @GET("users/{user}")
    suspend fun getAuthorDetails(
        @Path("user") user: String
    ): Response<GitAuthorResponse>

    @GET("repos/{user}/{repository}")
    suspend fun getRepositoryDetails(
        @Path("user") user: String,
        @Path("repository") repository: String
    ): Response<GitRepoDetailResponse>
}