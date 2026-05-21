package com.example.githubclient.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubclient.data.repository.GitHubRepository
import com.example.githubclient.data.datasource.AuthPreferences
import com.example.githubclient.data.remote.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: GitHubRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    val trendingRepos = repository.trendingRepos
    val searchResults = repository.searchResults
    val error = repository.error

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userRepos = MutableStateFlow<List<Repository>>(emptyList())
    val userRepos: StateFlow<List<Repository>> = _userRepos

    init {
        viewModelScope.launch {
            authPreferences.authTokenFlow.collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
                if (!token.isNullOrEmpty()) {
                    fetchUserRepos(token)
                }
            }
        }
        fetchTrendingRepos()
    }

    fun fetchTrendingRepos() {
        viewModelScope.launch {
            repository.fetchTrendingRepos()
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
}