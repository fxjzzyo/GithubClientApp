package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavController) {
    var token by remember { mutableStateOf("") }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    // ✅ 核心：登录成功后，自动返回上一页
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            delay(300) // 轻微延迟，让状态同步完成
            navController.popBackStack() // 自动关闭登录页，回到之前页面
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Login") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoggedIn) {
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("GitHub Personal Access Token") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(token) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            } else {
                // 登录成功时，显示提示（会自动关闭）
                Text(
                    "Login successful!",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}