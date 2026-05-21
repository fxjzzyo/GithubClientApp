package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavController) {
    var token by remember { mutableStateOf("") }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text("Login") }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoggedIn) {
                Text("You are logged in!", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { viewModel.logout() }) {
                    Text("Logout")
                }
                Button(onClick = { navController.navigate("profile") }) {
                    Text("Go to Profile")
                }
            } else {
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("GitHub Personal Access Token") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(token) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        }
    }
}