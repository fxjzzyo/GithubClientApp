package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
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
                // 🔥 左上角返回箭头
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(repo) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            repoDetail.value?.let {
                Text("名称：${it.full_name}", style = MaterialTheme.typography.titleMedium)
                Text("描述：${it.description ?: "无"}", Modifier.padding(vertical = 4.dp))
                Text("Star：${it.stargazers_count}")
                Text("语言：${it.language ?: "未知"}")
            }

            Text(
                "Create Issue",
                Modifier.padding(top = 20.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            if (!isLoggedIn) {
                Text("请先登录", color = MaterialTheme.colorScheme.error)
            } else {
                OutlinedTextField(
                    value = issueTitle,
                    onValueChange = { issueTitle = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxSize()
                )
                OutlinedTextField(
                    value = issueBody,
                    onValueChange = { issueBody = it },
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
                )
                Button(onClick = {
                    if (issueTitle.isNotBlank()) {
                        viewModel.createIssue(owner, repo, issueTitle, issueBody)
                    }
                }) {
                    Text("提交")
                }
            }
        }
    }
}