package com.zvonimirplivelic.githound.repository

import com.zvonimirplivelic.githound.remote.RetrofitInstance

class GitHoundRepository {
    suspend fun getRepositoryDetails(
        name: String,
        sortBy: String,
        sortOrder: String,
        resultsPerPage: Int,
        page: Int
    ) =
        RetrofitInstance.api.getRepositoryDetails(name, sortBy, sortOrder, resultsPerPage, page)
}