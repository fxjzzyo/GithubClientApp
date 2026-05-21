package com.example.githubclient


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.githubclient.data.datasource.AuthPreferences
import com.example.githubclient.data.repository.GitHubRepository
import com.example.githubclient.data.remote.RetrofitClient
import com.example.githubclient.ui.viewmodel.MainViewModel
import com.example.githubclient.ui.MainNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 依赖注入（简化版）
        val api = RetrofitClient.api
        val repository = GitHubRepository(api)
        val authPreferences = AuthPreferences(this)
        val viewModel = MainViewModel(repository, authPreferences)

        setContent {
            MaterialTheme {
                MainNavigation(viewModel)
            }
        }
    }
}
