package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel

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
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("$owner/$repo") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Repository Details", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(20.dp))

            if (isLoggedIn) {
                Text("Create New Issue", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = issueTitle,
                    onValueChange = { issueTitle = it },
                    label = { Text("Issue Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = issueBody,
                    onValueChange = { issueBody = it },
                    label = { Text("Issue Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.createIssue(owner, repo, issueTitle, issueBody)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Issue")
                }
            } else {
                Text("Login to create issues", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}