package com.zvonimirplivelic.githound

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zvonimirplivelic.githound.model.GitSearchListResponse
import com.zvonimirplivelic.githound.repository.GitHoundRepository
import com.zvonimirplivelic.githound.util.Constants.TIME_DELAY
import com.zvonimirplivelic.githound.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class GitHoundViewModel(app: Application) : AndroidViewModel(app) {
    private val gitHoundRepository: GitHoundRepository = GitHoundRepository()

    val repositoryList: MutableLiveData<Resource<GitSearchListResponse>> = MutableLiveData()
    private var repositoryListDataResponse: GitSearchListResponse? = null

    fun getRepositoryList(
        name: String,
        sortBy: String,
        sortOrder: String,
        resultsPerPage: Int,
        page: Int
    ) = viewModelScope.launch {
        safeRemoteRepositoryListCall(name, sortBy, sortOrder, resultsPerPage, page)
    }

    private suspend fun safeRemoteRepositoryListCall(
        name: String,
        sortBy: String,
        sortOrder: String,
        resultsPerPage: Int,
        page: Int
    ) {
        repositoryList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = gitHoundRepository.getRepositoryDetails(
                    name,
                    sortBy,
                    sortOrder,
                    resultsPerPage,
                    page
                )
                delay(TIME_DELAY)
                repositoryList.postValue(handleRepositoryListResponse(response))
            } else {
                repositoryList.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> repositoryList.postValue(Resource.Error("Network Failure"))
                else -> repositoryList.postValue(Resource.Error("Conversion Error: $t"))
            }
            Timber.d("Throwable: $t")
        }
    }

    private fun handleRepositoryListResponse(response: Response<GitSearchListResponse>): Resource<GitSearchListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                repositoryListDataResponse = resultResponse

                return Resource.Success(repositoryListDataResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<GitHoundApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}