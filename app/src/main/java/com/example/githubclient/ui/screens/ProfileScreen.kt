package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.githubclient.ui.viewmodel.MainViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.MoreVert

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val user by viewModel.userInfo.observeAsState(null)
    val userRepos by viewModel.userRepositories.observeAsState(emptyList())
    val error by viewModel.profileError.observeAsState()

    var menuExpanded by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    // ==========================================
    // 🔥 核心：未登录直接跳登录页
    // ==========================================
    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn { loggedIn ->
            isLoggedIn = loggedIn
            if (loggedIn) {
                viewModel.loadUserProfile()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyProfile") },
                navigationIcon = {}, // 平级页面 → 无返回
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, "菜单")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("退出登录", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.logout()
                                    navController.navigate("home") {
                                        popUpTo(0)
                                    }
                                }
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when {
                // ==========================================
                // 🔥 1. 还在检查登录状态 → 轻量 loading
                // ==========================================
                isLoggedIn == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // ==========================================
                // 🔥 2. 未登录 → 居中提示页（你要的效果）
                // ==========================================
                isLoggedIn == false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "尚未登录",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "请登录后查看个人资料",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                navController.navigate("login")
                            }
                        ) {
                            Text("去登录")
                        }
                    }
                }

                // 加载中
                user == null && error == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                // 错误 + 重试
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUserProfile() }) {
                            Text("重试")
                        }
                    }
                }

                // 已登录，显示资料
                else -> {
                    // 头像
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(user?.avatar_url),
                            contentDescription = "头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                user?.name ?: user?.login ?: "用户",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("@${user?.login}")
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    InfoRow("我的仓库", "${userRepos.size} 个")

                    Spacer(Modifier.height(32.dp))
                    Text("仓库列表", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    if (userRepos.isEmpty()) {
                        Text("暂无仓库")
                    } else {
                        userRepos.forEach { repo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        val route = "repoDetail/${repo.owner.login}/${repo.name}"
                                        navController.navigate(route)
                                    }
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(repo.name, fontWeight = FontWeight.Medium)
                                    Text(
                                        repo.description ?: "无描述",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}