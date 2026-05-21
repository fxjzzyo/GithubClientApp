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
import com.example.githubclient.data.remote.Repository
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
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            val data = repoDetail.value
            if (data == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading...", style = MaterialTheme.typography.bodyLarge)
                }
                return@Column
            }

            Text(
                text = "仓库信息",
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

            Text(
                "仓库描述",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = data.description ?: "暂无描述",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Text(
                "创建 Issue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            if (!isLoggedIn) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "登录后方可提交Issue反馈",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("login") },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.widthIn(min = 180.dp)
                    ) {
                        Text("立即前往登录")
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

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}