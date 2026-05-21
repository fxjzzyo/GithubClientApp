package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var language by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val error by viewModel.searchError.observeAsState()

    var isLoading by remember { mutableStateOf(false) }
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
                    !hasSearched -> {
                        Text(
                            "输入 Language 搜索仓库",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    // 错误 + 重试
                    error != null -> {
                        Column(
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
                                isLoading = true
                                viewModel.searchReposByLanguage(language)
                            }) {
                                Text("重试")
                            }
                        }
                    }

                    searchResults.isEmpty() -> {
                        Text("未找到相关仓库", style = MaterialTheme.typography.bodyLarge)
                    }

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

    LaunchedEffect(searchResults, error) {
        isLoading = false
    }
}