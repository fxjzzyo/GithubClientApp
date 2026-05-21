package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    viewModel: MainViewModel,
    owner: String,
    repo: String,
    navController: NavController
) {
    var issueTitle by remember { mutableStateOf("") }
    var issueBody by remember { mutableStateOf("") }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    val repoDetail by viewModel.repoDetail.observeAsState()
    val error by viewModel.detailError.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRepoDetail(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                title = { Text(repo, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when {
                // 加载中
                repoDetail == null && error == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                // 失败 + 重试
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.fetchRepoDetail(owner, repo)
                        }) {
                            Text("重试")
                        }
                    }
                }

                // 成功
                else -> {
                    val data = repoDetail!!
                    Text(
                        "仓库信息",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    InfoRow("仓库名称", data.full_name)
                    InfoRow("作者", data.owner.login)
                    InfoRow("Star 数量", "${data.stargazers_count} ⭐")
                    InfoRow("开发语言", data.language ?: "未指定")
                    InfoRow("默认分支", data.default_branch ?: "main")
                    InfoRow("可见性", if (data.private == true) "私有" else "公开")
                    InfoRow("Issues", "${data.open_issues_count ?: 0} 个开放")
                    InfoRow("仓库大小", "${(data.size ?: 0) / 1024} MB")

                    Spacer(Modifier.height(24.dp))
                    Text("仓库描述", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        data.description ?: "暂无描述",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(32.dp))
                    Text("创建 Issue", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    if (!isLoggedIn) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("登录后方可提交 Issue")
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { navController.navigate("login") }) {
                                Text("立即登录")
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = issueTitle,
                            onValueChange = { issueTitle = it },
                            label = { Text("Issue 标题") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = issueBody,
                            onValueChange = { issueBody = it },
                            label = { Text("Issue 内容（可选）") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (issueTitle.isNotBlank()) {
                                    viewModel.createIssue(owner, repo, issueTitle, issueBody)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("提交 Issue")
                        }
                    }
                }
            }
        }
    }
}