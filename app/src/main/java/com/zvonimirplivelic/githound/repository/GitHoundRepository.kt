package com.zvonimirplivelic.githound.repository

import com.zvonimirplivelic.githound.remote.RetrofitInstance

class GitHoundRepository {
    suspend fun getRepositoryList() =
        RetrofitInstance.api.getRepositoryList()

    suspend fun getAuthorDetails(user: String) =
        RetrofitInstance.api.getAuthorDetails(user)

    suspend fun getRepositoryDetails(user: String, repository: String) =
        RetrofitInstance.api.getRepositoryDetails(user, repository)
}