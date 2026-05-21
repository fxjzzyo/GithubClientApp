package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
        topBar = { TopAppBar(title = { Text("My Profile") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!isLoggedIn.value) {
                Text("Please login first!")
                return@Column
            }

            currentUser?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = it.avatar_url,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(64.dp)
                    )
                    Column {
                        Text(it.login, style = MaterialTheme.typography.titleLarge)
                        it.name?.let { name ->
                            Text(name)
                        }
                    }
                }
            }

            Text(
                "My Repositories",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn {
                items(userRepos.value) { repo ->
                    RepoItem(repo) {
                        navController.navigate("repoDetail/${repo.owner.login}/${repo.name}")
                    }
                }
            }
        }
    }
}