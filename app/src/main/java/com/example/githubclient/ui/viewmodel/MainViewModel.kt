package com.example.githubclient.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubclient.data.repository.GitHubRepository
import com.example.githubclient.data.datasource.AuthPreferences
import com.example.githubclient.data.remote.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.githubclient.data.remote.User


class MainViewModel(
    private val repository: GitHubRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    val trendingRepos = repository.trendingRepos
    val searchResults = repository.searchResults

    private val _homeError = MutableLiveData<String?>(null)
    val homeError: LiveData<String?> = _homeError

    private val _searchError = MutableLiveData<String?>(null)
    val searchError: LiveData<String?> = _searchError

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userRepos = MutableStateFlow<List<Repository>>(emptyList())
    val userRepos: StateFlow<List<Repository>> = _userRepos

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    init {
        viewModelScope.launch {
            authPreferences.authTokenFlow.collect { token ->
                val logged = !token.isNullOrEmpty()
                _isLoggedIn.value = logged

                if (logged) {
                    fetchUserRepos(token)
                    fetchAuthenticatedUser(token) // 👈 这里调用了！
                } else {
                    _userRepos.value = emptyList()
                    _currentUser.postValue(null)
                }
            }
        }
        fetchTrendingRepos()
    }

    // 获取当前登录用户信息（补全的方法）
    private fun fetchAuthenticatedUser(token: String) {
        viewModelScope.launch {
            val user = repository.getAuthenticatedUser(token)
            _currentUser.postValue(user)
        }
    }

    fun fetchTrendingRepos() {
        viewModelScope.launch {
            _homeError.postValue(null) // 清空首页错误
            repository.fetchTrendingRepos()
        }
    }

    fun searchReposByLanguage(language: String) {
        viewModelScope.launch {
            _searchError.postValue(null) // 清空搜索错误
            repository.searchRepositoriesByLanguage(language)
        }
    }

    private fun fetchUserRepos(token: String) {
        viewModelScope.launch {
            val repos = repository.getUserRepositories(token)
            repos?.let { _userRepos.value = it }
        }
    }

    fun login(token: String) {
        viewModelScope.launch {
            authPreferences.saveAuthToken(token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPreferences.clearAuthToken()
            _userRepos.value = emptyList()
        }
    }

    fun createIssue(owner: String, repo: String, title: String, body: String?) {
        viewModelScope.launch {
            authPreferences.authTokenFlow.collect { token ->
                token?.let {
                    repository.createIssue(it, owner, repo, title, body)
                }
            }
        }
    }


    private val _repoDetail = MutableLiveData<Repository?>()
    val repoDetail: LiveData<Repository?> = _repoDetail

    fun fetchRepoDetail(owner: String, repo: String) {
        viewModelScope.launch {
            val result = repository.getRepositoryDetail(owner, repo)
            _repoDetail.postValue(result)
        }
    }
}