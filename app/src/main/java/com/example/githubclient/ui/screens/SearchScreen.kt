package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.runtime.livedata.observeAsState
import android.widget.Toast
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var language by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val error by viewModel.searchError.observeAsState()

    // 核心：是否正在加载（每次点击搜索都强制为 true）
    var isLoading by remember { mutableStateOf(false) }
    // 是否点击过搜索（控制显示提示还是结果）
    var hasSearched by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                label = { Text("Language") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Row {
                        if (language.isNotEmpty()) {
                            IconButton(onClick = {
                                language = ""
                                hasSearched = false
                                isLoading = false
                            }) {
                                Icon(Icons.Default.Close, "清空")
                            }
                        }

                        IconButton(onClick = {
                            if (language.isBlank()) {
                                Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
                            } else {
                                // 🔥 每次点击搜索，强制显示 Loading
                                isLoading = true
                                hasSearched = true
                                viewModel.searchReposByLanguage(language)
                            }
                        }) {
                            Icon(Icons.Default.Search, "搜索")
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // 1. 未点击搜索 → 提示
                    !hasSearched -> {
                        Text(
                            "输入 Language 搜索仓库",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 2. 🔥 正在加载 → 无论如何都显示 Loading（修复关键）
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    // 3. 加载完成但无结果
                    searchResults.isEmpty() && error == null -> {
                        Text("未找到相关仓库", style = MaterialTheme.typography.bodyLarge)
                    }

                    // 4. 错误
                    error != null -> {
                        Text("搜索失败，请稍后重试", color = MaterialTheme.colorScheme.error)
                    }

                    // 5. 显示列表
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(searchResults) { repo ->
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

    // 监听结果变化，加载完成后关闭 Loading
    LaunchedEffect(searchResults, error) {
        if (hasSearched) {
            isLoading = false
        }
    }
}