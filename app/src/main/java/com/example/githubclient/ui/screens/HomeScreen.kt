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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repos by viewModel.trendingRepos.observeAsState(emptyList())
    val error by viewModel.homeError.observeAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.fetchTrendingRepos()
    }

    // 屏幕方向判断
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // 横屏模式：选中的 repo
    var selectedRepo by remember { mutableStateOf<Repository?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending Repositories") }
            )
        }
    ) { padding ->

        if (isLandscape) {
            // ==========================================
            // 🔥 横屏：双面板  列表 + 详情
            // ==========================================
            Row(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // 左侧列表 (占 40% 宽度)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.4f)
                        .padding(16.dp)
                ) {
                    when {
                        error != null -> {
                            item {
                                ErrorRetryView(
                                    message = error!!,
                                    onRetry = { viewModel.fetchTrendingRepos() }
                                )
                            }
                        }
                        repos.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        else -> {
                            items(repos) { repo ->
                                RepoItem(
                                    repo = repo,
                                    isSelected = repo == selectedRepo,
                                    onClick = {
                                        selectedRepo = repo
                                    }
                                )
                            }
                        }
                    }
                }

                // 右侧详情 (占 60% 宽度)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                        .padding(16.dp)
                ) {
                    if (selectedRepo == null) {
                        Text(
                            text = "请选择一个仓库查看详情",
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            RepoDetailContent(
                                repo = selectedRepo!!,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }

        } else {
            // ==========================================
            // 竖屏：正常单列表
            // ==========================================
            LazyColumn(
                state = listState,
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when {
                    error != null -> {
                        item {
                            ErrorRetryView(
                                message = error!!,
                                onRetry = { viewModel.fetchTrendingRepos() }
                            )
                        }
                    }
                    repos.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    else -> {
                        items(repos) { repo ->
                            RepoItem(repo = repo) {
                                navController.navigate("repoDetail/${repo.owner.login}/${repo.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 错误重试 UI
@Composable
fun ErrorRetryView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
fun RepoItem(
    repo: Repository,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        colors = if (isSelected) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) else CardDefaults.cardColors()
    ) {
        Column(Modifier.padding(16.dp)) {
            // 仓库名称
            Text(
                text = repo.name,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            // 描述
            Text(
                text = repo.description ?: "无描述",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            // 底部信息：Star + 语言 + 作者
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⭐ ${repo.stargazers_count}")
                Text(repo.language ?: "Unknown")
                Text("@${repo.owner.login}")
            }
        }
    }
}