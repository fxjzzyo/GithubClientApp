package com.example.githubclient.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubclient.data.remote.GitHubApiService
import com.example.githubclient.data.remote.IssueRequest
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.data.remote.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubRepository(private val api: GitHubApiService) {

    private val _trendingRepos = MutableLiveData<List<Repository>>()
    val trendingRepos: LiveData<List<Repository>> = _trendingRepos

    private val _searchResults = MutableLiveData<List<Repository>>()
    val searchResults: LiveData<List<Repository>> = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    suspend fun fetchTrendingRepos() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getTrendingRepositories()
                if (response.isSuccessful) {
                    _trendingRepos.postValue(response.body()?.items ?: emptyList())
                } else {
                    _error.postValue("Failed to fetch trending repos: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    suspend fun searchRepositoriesByLanguage(language: String) {
        withContext(Dispatchers.IO) {
            try {
                val query = "language:$language"
                val response = api.searchRepositories(query)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body()?.items ?: emptyList())
                } else {
                    _error.postValue("Search failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    suspend fun getUserRepositories(token: String): List<Repository>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserRepositories("token $token")
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _error.postValue("Failed to fetch user repos: ${e.message}")
                null
            }
        }
    }

    suspend fun createIssue(
        token: String,
        owner: String,
        repo: String,
        title: String,
        body: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createIssue(
                    "token $token",
                    owner,
                    repo,
                    IssueRequest(title, body)
                )
                response.isSuccessful
            } catch (e: Exception) {
                _error.postValue("Failed to create issue: ${e.message}")
                false
            }
        }
    }

    suspend fun getRepositoryDetail(owner: String, repo: String): Repository? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getRepositoryDetail(owner, repo)
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _error.postValue("获取详情失败：${e.message}")
                null
            }
        }
    }

    suspend fun getAuthenticatedUser(token: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAuthenticatedUser("token $token")
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _error.postValue("获取用户信息失败：${e.message}")
                null
            }
        }
    }
}