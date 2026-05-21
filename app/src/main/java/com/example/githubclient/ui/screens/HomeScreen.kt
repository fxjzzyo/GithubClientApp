package com.example.githubclient.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val repos = viewModel.trendingRepos.observeAsState(emptyList())
    val error = viewModel.homeError.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trending Repositories") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 加载中 Loading
            if (repos.value.isEmpty() && error.value == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 错误
            else if (error.value != null) {
                Text(
                    text = "Error: ${error.value}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            // 列表
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(repos.value) { repo ->
                        RepoItem(repo) {
                            navController.navigate("repoDetail/${repo.owner.login}/${repo.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepoItem(repo: Repository, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = repo.full_name, style = MaterialTheme.typography.titleMedium)
            Text(text = repo.description ?: "No description")
            Text("⭐ ${repo.stargazers_count} • ${repo.language ?: "Unknown"}")
        }
    }
}