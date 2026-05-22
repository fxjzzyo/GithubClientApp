package com.example.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalConfiguration
import com.example.githubclient.data.remote.Repository
import com.example.githubclient.data.remote.User

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

    // 屏幕方向
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
                navigationIcon = {},
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

        if (isLandscape) {
            // 横屏：左右双栏（不变）
            Row(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    ProfileContent(
                        user = user,
                        userRepos = userRepos,
                        isLoggedIn = isLoggedIn,
                        error = error,
                        onRetry = { viewModel.loadUserProfile() }
                    )
                }

                ProfileRepoList(
                    modifier = Modifier.weight(0.6f),
                    userRepos = userRepos,
                    navController = navController
                )
            }
        } else {
            // 竖屏：修复！不用外层 verticalScroll，用 LazyColumn 全包
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // 1. 个人信息部分
                item {
                    ProfileContent(
                        user = user,
                        userRepos = userRepos,
                        isLoggedIn = isLoggedIn,
                        error = error,
                        onRetry = { viewModel.loadUserProfile() }
                    )
                }

                // 2. 仓库列表标题
                item {
                    if (isLoggedIn == true && user != null && error == null) {
                        Spacer(Modifier.height(32.dp))
                        Text(
                            "仓库列表",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // 3. 仓库列表内容
                if (isLoggedIn == true && user != null && error == null) {
                    if (userRepos.isEmpty()) {
                        item { Text("暂无仓库") }
                    } else {
                        items(userRepos) { repo ->
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
                                    Text(repo.description ?: "无描述", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 个人信息（只负责上半部分，不包含仓库列表）
@Composable
fun ProfileContent(
    user: User?,
    userRepos: List<Repository>,
    isLoggedIn: Boolean?,
    error: String?,
    onRetry: () -> Unit
) {
    when {
        isLoggedIn == null -> {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        isLoggedIn == false -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
            }
        }
        user == null && error == null -> {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) { Text("重试") }
            }
        }
        else -> {
            // 头像 + 信息
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
        }
    }
}

// 横屏右侧仓库列表（不变）
@Composable
fun ProfileRepoList(
    modifier: Modifier,
    userRepos: List<Repository>,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        item {
            Text(
                "仓库列表",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
        }
        if (userRepos.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无仓库")
                }
            }
        } else {
            items(userRepos) { repo ->
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
                        Text(repo.description ?: "无描述", style = MaterialTheme.typography.bodyMedium)
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