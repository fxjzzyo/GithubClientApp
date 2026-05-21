package com.example.githubclient.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubclient.data.remote.GitHubApiService
import com.example.githubclient.data.remote.IssueRequest
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.data.remote.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.io.IOException

class GitHubRepository(private val api: GitHubApiService) {

    private val _trendingRepos = MutableLiveData<List<Repository>>()
    val trendingRepos: LiveData<List<Repository>> = _trendingRepos

    private val _searchResults = MutableLiveData<List<Repository>>()
    val searchResults: LiveData<List<Repository>> = _searchResults

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    private val _userRepos = MutableLiveData<List<Repository>>()
    val userRepos: LiveData<List<Repository>> = _userRepos


    private val _homeError = MutableLiveData<String?>()
    val homeError: LiveData<String?> = _homeError

    private val _searchError = MutableLiveData<String?>()
    val searchError: LiveData<String?> = _searchError

    private val _detailError = MutableLiveData<String?>()
    val detailError: LiveData<String?> = _detailError

    private val _profileError = MutableLiveData<String?>()
    val profileError: LiveData<String?> = _profileError



    // ======================
    // 首页请求
    // ======================
    suspend fun fetchTrendingRepos() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getTrendingRepositories()
                if (response.isSuccessful) {
                    _trendingRepos.postValue(response.body()?.items ?: emptyList())
                    _homeError.postValue(null) // 成功清空错误
                } else {
                    _homeError.postValue("Failed to fetch trending repos: ${response.message()}")
                }
            } catch (e: SocketTimeoutException) {
                _homeError.postValue("网络超时，请检查网络")
            } catch (e: IOException) {
                _homeError.postValue("网络异常，请检查网络")
            } catch (e: Exception) {
                _homeError.postValue("Network error: ${e.message}")
            }
        }
    }

    // ======================
    // 搜索请求
    // ======================
    suspend fun searchRepositoriesByLanguage(language: String) {
        withContext(Dispatchers.IO) {
            try {
                val query = "language:$language"
                val response = api.searchRepositories(query)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body()?.items ?: emptyList())
                    _searchError.postValue(null) // 成功清空错误
                } else {
                    _searchError.postValue("Search failed: ${response.message()}")
                }
            } catch (e: SocketTimeoutException) {
                _searchError.postValue("网络超时，请检查网络")
            } catch (e: IOException) {
                _searchError.postValue("网络异常，请检查网络")
            } catch (e: Exception) {
                _searchError.postValue("Network error: ${e.message}")
            }
        }
    }

    // ======================
    // 仓库详情
    // ======================
    suspend fun getRepositoryDetail(owner: String, repo: String): Repository? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getRepositoryDetail(owner, repo)
                if (response.isSuccessful) {
                    _detailError.postValue(null)
                    response.body()
                } else {
                    _detailError.postValue("获取详情失败：${response.message()}")
                    null
                }
            } catch (e: SocketTimeoutException) {
                _detailError.postValue("网络超时，请检查网络")
                null
            } catch (e: IOException) {
                _detailError.postValue("网络异常，请检查网络")
                null
            } catch (e: Exception) {
                _detailError.postValue("获取详情失败：${e.message}")
                null
            }
        }
    }

    // ==========================================
    // 🔥 Profile 加载（带超时 + 错误）
    // ==========================================
    suspend fun fetchUserProfile(token: String) {
        withContext(Dispatchers.IO) {
            try {
                _profileError.postValue(null) // 清空错误

                val userResponse = api.getAuthenticatedUser("token $token")
                val reposResponse = api.getUserRepositories("token $token")

                if (userResponse.isSuccessful && reposResponse.isSuccessful) {
                    _userInfo.postValue(userResponse.body())
                    _userRepos.postValue(reposResponse.body() ?: emptyList())
                } else {
                    _profileError.postValue("加载个人资料失败")
                }

            } catch (e: SocketTimeoutException) {
                _profileError.postValue("网络超时，请检查网络后重试")
            } catch (e: IOException) {
                _profileError.postValue("网络异常，请检查网络后重试")
            } catch (e: Exception) {
                _profileError.postValue("加载失败，请重试")
            }
        }
    }

    // ======================
    // 以下保持你原有代码不变
    // ======================
    suspend fun getUserRepositories(token: String): List<Repository>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserRepositories("token $token")
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _detailError.postValue("Failed to fetch user repos: ${e.message}")
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
                _detailError.postValue("Failed to create issue: ${e.message}")
                false
            }
        }
    }

    suspend fun getAuthenticatedUser(token: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAuthenticatedUser("token $token")
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _detailError.postValue("获取用户信息失败：${e.message}")
                null
            }
        }
    }

    fun clearProfileData() {
        _userInfo.postValue(null)
        _userRepos.postValue(emptyList())
        _profileError.postValue(null)
    }

}