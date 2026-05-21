package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.githubclient.ui.viewmodel.MainViewModel
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel, navController: NavController) {
    val userRepos = viewModel.userRepos.collectAsState(initial = emptyList())
    val isLoggedIn = viewModel.isLoggedIn.collectAsState(initial = false)
    val currentUser = viewModel.currentUser.observeAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    if (isLoggedIn.value) {
                        IconButton(onClick = {
                            viewModel.logout()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            if (!isLoggedIn.value) {
                // 未登录居中布局
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "暂无登录账号",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "登录后即可查看个人仓库与账号信息",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.widthIn(min = 160.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("前往登录")
                    }
                }
                return@Column
            }

            // 登录后原有布局
            currentUser?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = it.avatar_url,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(64.dp)
                    )
                    Column {
                        Text(it.login, style = MaterialTheme.typography.titleLarge)
                        it.name?.let { name ->
                            Text(name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Text(
                "My Repositories",
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(userRepos.value) { repo ->
                    RepoItem(repo) {
                        navController.navigate("repoDetail/${repo.owner.login}/${repo.name}")
                    }
                }
            }
        }
    }
}