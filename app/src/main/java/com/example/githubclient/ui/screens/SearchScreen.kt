package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.githubclient.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel) {
    var language by remember { mutableStateOf("Kotlin") }
    val searchResults = viewModel.searchResults.observeAsState(emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("Search Repositories") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                label = { Text("Programming Language") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchReposByLanguage(language) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
            LazyColumn {
                items(searchResults.value) { repo ->
                    RepoItem(repo) {}
                }
            }
        }
    }
}