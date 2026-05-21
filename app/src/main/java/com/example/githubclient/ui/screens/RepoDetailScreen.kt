package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.runtime.livedata.observeAsState

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
    val repoDetail = viewModel.repoDetail.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRepoDetail(owner, repo)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(repo) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 显示仓库详情
            repoDetail.value?.let {
                Text("名称：${it.full_name}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("描述：${it.description ?: "无描述"}")
                Spacer(Modifier.height(8.dp))
                Text("Star：${it.stargazers_count}")
                Spacer(Modifier.height(8.dp))
                Text("语言：${it.language ?: "未知"}")
                Spacer(Modifier.height(24.dp))
            }

            // 创建 Issue
            Text("创建 Issue", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (!isLoggedIn) {
                Text("请先登录！", color = MaterialTheme.colorScheme.error)
            } else {
                OutlinedTextField(
                    value = issueTitle,
                    onValueChange = { issueTitle = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = issueBody,
                    onValueChange = { issueBody = it },
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (issueTitle.isNotBlank()) {
                            viewModel.createIssue(owner, repo, issueTitle, issueBody)
                            issueTitle = ""
                            issueBody = ""
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