package com.example.githubclient.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.githubclient.ui.screens.HomeScreen
import com.example.githubclient.ui.screens.LoginScreen
import com.example.githubclient.ui.screens.ProfileScreen
import com.example.githubclient.ui.screens.RepoDetailScreen
import com.example.githubclient.ui.screens.SearchScreen
import com.example.githubclient.ui.viewmodel.MainViewModel

@Composable
fun MainNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    MainScaffold(navController = navController) { modifier ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier
        ) {
            composable("home") { HomeScreen(viewModel, navController) }
            composable("search") { SearchScreen(viewModel, navController) }
            composable("profile") { ProfileScreen(viewModel, navController) }
            composable("login") { LoginScreen(viewModel, navController) }
            composable("repoDetail/{owner}/{repo}") { backStackEntry ->
                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                val repo = backStackEntry.arguments?.getString("repo") ?: ""
                RepoDetailScreen(viewModel, owner, repo, navController)
            }
        }
    }
}