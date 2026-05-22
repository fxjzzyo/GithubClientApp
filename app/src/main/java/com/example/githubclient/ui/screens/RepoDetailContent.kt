package com.example.githubclient.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.ui.viewmodel.MainViewModel

@Composable
fun RepoDetailContent(
    repo: Repository,
    viewModel: MainViewModel,
    navController: NavController
) {
    var issueTitle by remember { mutableStateOf("") }
    var issueBody by remember { mutableStateOf("") }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)
    val context = LocalContext.current

    // 监听提交结果
    LaunchedEffect(Unit) {
        viewModel.issueCreateResult.collect { isSuccess ->
            if (isSuccess) {
                // 成功：清空输入框 + Toast
                issueTitle = ""
                issueBody = ""
                Toast.makeText(context, "Issue 提交成功", Toast.LENGTH_SHORT).show()
            } else {
                // 失败：Toast
                Toast.makeText(context, "Issue 提交失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "仓库信息",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        InfoRow("仓库名称", repo.full_name)
        InfoRow("作者", repo.owner.login)
        InfoRow("Star 数量", "${repo.stargazers_count} ⭐")
        InfoRow("开发语言", repo.language ?: "未指定")
        InfoRow("默认分支", repo.default_branch ?: "main")
        InfoRow("可见性", if (repo.private == true) "私有" else "公开")
        InfoRow("Issues", "${repo.open_issues_count ?: 0} 个开放")
        InfoRow("仓库大小", "${(repo.size ?: 0) / 1024} MB")

        Spacer(Modifier.height(24.dp))
        Text("仓库描述", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            repo.description ?: "暂无描述",
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
                        viewModel.createIssue(repo.owner.login, repo.name, issueTitle, issueBody)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("提交 Issue")
            }
        }
    }
}