package com.example.githubclient.data.remote

import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {
    // 获取热门仓库
    @GET("search/repositories?q=stars:>1000&sort=stars&order=desc")
    suspend fun getTrendingRepositories(): Response<RepoSearchResponse>

    // 按语言搜索仓库
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): Response<RepoSearchResponse>

    // 获取仓库详情
    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetail(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Repository>

    // 获取用户仓库列表（登录后）
    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String
    ): Response<List<Repository>>

    // 创建Issue（登录后）
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueRequest: IssueRequest
    ): Response<Issue>

    // 获取用户信息（登录后）
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String
    ): Response<User>
}

// 数据模型
data class RepoSearchResponse(val items: List<Repository>)
data class Repository(
    val id: Long,
    val name: String,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?,
    val owner: Owner,
    val private: Boolean?,
    val default_branch: String?,
    val open_issues_count: Int?,
    val size: Long?
)
data class Owner(val login: String, val avatar_url: String)
data class User(val login: String, val avatar_url: String, val name: String?)
data class IssueRequest(val title: String, val body: String?)
data class Issue(val id: Long, val title: String, val html_url: String)