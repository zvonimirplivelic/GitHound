package com.zvonimirplivelic.githound

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zvonimirplivelic.githound.model.GitAuthorResponse
import com.zvonimirplivelic.githound.model.GitRepoDetailResponse
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.repository.GitHoundRepository
import com.zvonimirplivelic.githound.util.Constants.TIME_DELAY
import com.zvonimirplivelic.githound.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class GitHoundViewModel(app: Application) : AndroidViewModel(app) {
    private val gitHoundRepository: GitHoundRepository = GitHoundRepository()

    val repositoryList: MutableLiveData<Resource<GitRepoListResponse>> = MutableLiveData()
    val repositoryDetails: MutableLiveData<Resource<GitRepoDetailResponse>> = MutableLiveData()
    val authorDetails: MutableLiveData<Resource<GitAuthorResponse>> = MutableLiveData()

    private var repositoryListDataResponse: GitRepoListResponse? = null
    private var repositoryDetailsResponse: GitRepoDetailResponse? = null
    private var authorDetailsResponse: GitAuthorResponse? = null

    fun getRepositoryList() = viewModelScope.launch {
        safeRemoteRepositoryListCall()
    }

    fun getRepositoryDetailsResponse(authorName: String, repositoryName: String) =
        viewModelScope.launch {
            safeRemoteRepositoryDetailsCall(authorName, repositoryName)
        }

    fun getAuthorDetailsResponse(authorName: String) =
        viewModelScope.launch {
            safeRemoteAuthorDetailsCall(authorName)
        }

    private suspend fun safeRemoteRepositoryListCall() {
        repositoryList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = gitHoundRepository.getRepositoryList()
                delay(TIME_DELAY)
                repositoryList.postValue(handleRepositoryListResponse(response))
            } else {
                repositoryList.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> repositoryList.postValue(Resource.Error("Network Failure"))
                else -> repositoryList.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeRemoteRepositoryDetailsCall(
        authorName: String,
        repositoryName: String
    ) {
        repositoryDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = gitHoundRepository.getRepositoryDetails(authorName, repositoryName)
                delay(TIME_DELAY)
                repositoryDetails.postValue(handleRepositoryDetailsResponse(response))
            } else {
                repositoryDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> repositoryDetails.postValue(Resource.Error("Network Failure"))
                else -> repositoryDetails.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeRemoteAuthorDetailsCall(authorName: String) {
        authorDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = gitHoundRepository.getAuthorDetails(authorName)
                delay(TIME_DELAY)
                authorDetails.postValue(handleAuthorDetailsResponse(response))
            } else {
                authorDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> authorDetails.postValue(Resource.Error("Network Failure"))
                else -> authorDetails.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleRepositoryListResponse(response: Response<GitRepoListResponse>): Resource<GitRepoListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                repositoryListDataResponse = resultResponse

                return Resource.Success(repositoryListDataResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleRepositoryDetailsResponse(response: Response<GitRepoDetailResponse>): Resource<GitRepoDetailResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                repositoryDetailsResponse = resultResponse

                return Resource.Success(repositoryDetailsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleAuthorDetailsResponse(response: Response<GitAuthorResponse>): Resource<GitAuthorResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                authorDetailsResponse = resultResponse

                return Resource.Success(authorDetailsResponse ?: resultResponse)
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