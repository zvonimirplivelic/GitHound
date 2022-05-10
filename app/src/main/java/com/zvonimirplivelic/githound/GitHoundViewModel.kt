package com.zvonimirplivelic.githound

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private var repositoryListDataResponse: GitRepoListResponse? = null

    fun getRepositoryList() = viewModelScope.launch {
        safeRemoteDataCall()
    }

    private suspend fun safeRemoteDataCall() {
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

    private fun handleRepositoryListResponse(response: Response<GitRepoListResponse>): Resource<GitRepoListResponse> {
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