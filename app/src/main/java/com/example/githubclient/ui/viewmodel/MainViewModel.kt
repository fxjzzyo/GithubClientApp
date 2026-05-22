package com.example.githubclient.ui.viewmodel

import android.widget.Toast
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull


class MainViewModel(
    private val repository: GitHubRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    val trendingRepos = repository.trendingRepos
    val searchResults = repository.searchResults
    val userInfo = repository.userInfo
    val userRepositories = repository.userRepos


    val homeError: LiveData<String?> = repository.homeError
    val searchError: LiveData<String?> = repository.searchError
    val detailError: LiveData<String?> = repository.detailError
    val profileError: LiveData<String?> = repository.profileError

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userRepos = MutableStateFlow<List<Repository>>(emptyList())
    val userRepos: StateFlow<List<Repository>> = _userRepos

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    private val _issueCreateResult = MutableSharedFlow<Boolean>()
    val issueCreateResult = _issueCreateResult.asSharedFlow()

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
            repository.fetchTrendingRepos()
        }
    }

    // ==========================================
    // 检查是否登录（给 Profile 用）
    // ==========================================
    fun isUserLoggedIn(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = authPreferences.authTokenFlow.firstOrNull()
            callback(!token.isNullOrEmpty())
        }
    }

    // ==========================================
    // 从 DataStore 获取 token 并加载资料
    // ==========================================
    fun loadUserProfile() {
        viewModelScope.launch {
            val token = authPreferences.authTokenFlow.firstOrNull()
            if (token.isNullOrEmpty()) {
                return@launch
            }
            repository.fetchUserProfile(token)
        }
    }

    fun searchReposByLanguage(language: String) {
        viewModelScope.launch {
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
            repository.fetchUserProfile(token)
            authPreferences.saveAuthToken(token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPreferences.clearAuthToken()
            _userRepos.value = emptyList()
            repository.clearProfileData()
        }
    }

    fun createIssue(owner: String, repo: String, title: String, body: String?) {
        viewModelScope.launch {
            authPreferences.authTokenFlow.collect { token ->
                token?.let {
                    val success = repository.createIssue(it, owner, repo, title, body)
                    _issueCreateResult.emit(success)
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