package com.zvonimirplivelic.githound.remote

import com.zvonimirplivelic.githound.model.GitSearchListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHoundService {
    @GET("search/repositories")
    suspend fun getRepositoryDetails(
        @Query("q") name: String,
        @Query("sort") sortBy: String,
        @Query("order") orderBy: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Response<GitSearchListResponse>
}