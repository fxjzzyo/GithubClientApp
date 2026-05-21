package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel, navController: NavController) {
    // ✅ 通用可用，所有项目都支持
    val userRepos = viewModel.userRepos.collectAsState(initial = emptyList())
    val isLoggedIn = viewModel.isLoggedIn.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ✅ 核心修复：!isLoggedIn 不再红线
            if (!isLoggedIn.value) {
                Text("Please login first!")
                return@Column
            }

            Text(
                text = "My Repositories",
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(userRepos.value) { repo ->
                    RepoItem(repo) { }
                }
            }
        }
    }
}