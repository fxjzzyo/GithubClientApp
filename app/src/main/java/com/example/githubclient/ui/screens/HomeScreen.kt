package com.example.githubclient.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val repos by viewModel.trendingRepos.observeAsState(emptyList())
    val error by viewModel.homeError.observeAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trending Repositories") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                // 加载中
                repos.isEmpty() && error == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 失败 + 重试按钮
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.fetchTrendingRepos()
                        }) {
                            Text("重试")
                        }
                    }
                }

                // 成功列表
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(repos) { repo ->
                            RepoItem(repo) {
                                navController.navigate("repoDetail/${repo.owner.login}/${repo.name}")
                            }
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repo.full_name, style = MaterialTheme.typography.titleMedium)
            Text(text = repo.description ?: "No description")
            Text("⭐ ${repo.stargazers_count} • ${repo.language ?: "Unknown"}")
        }
    }
}