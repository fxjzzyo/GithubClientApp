# GitHub Android Client

**English | [中文](README.md)**

## 📖 Introduction

This is a modern third-party GitHub client for Android built entirely with Jetpack Compose and MVVM architecture. It uses Retrofit for network requests and DataStore for lightweight local data persistence.
The app supports core GitHub functions including trending repository browsing, language-based repository search, user profile viewing, issue creation, and GitHub token login.
It implements complete network exception handling, including timeout, network error, server error prompts and retry logic, with unified UI state management for loading, error, empty and success status.

## ✨ Features
- Trending repository browsing: View the list of popular open-source repositories
- Repository search: Filter repositories by programming language
- Repository details display: Show stars, branches, size and other basic info
- Page navigation: Jump to detail page by clicking repository item
- GitHub account sign-in: Authenticate login with personal access token
- Personal profile display: Check user information and owned repositories after login
- Issue creation: Submit new issues to target repositories with logged account
- Account logout: Sign out and clear local authentication data
- Network exception handling: Support retry operation under offline and timeout conditions
- Multi UI state: Render loading, error, empty and normal content layout
- Landscape mode: left is list, right is detail panel

## 🛠 Tech Stack
- UI：Jetpack Compose (Material3)
- Architecture：MVVM,ViewModel,LiveData
- Network：Retrofit2 + OkHttp3
- Asyn：Kotlin Coroutine + Flow
- Data：DataStore
- ImageLoad：Coil
- Navigation：Jetpack Navigation

## 📦 Build Guide
1. Environment Requirements
- Android Studio Hedgehog or newer
- Kotlin 1.9+
- AGP 8.0+
- Min SDK 29
2. Clone Repositorygit clone https://github.com/fxjzzyo/GithubClientApp.git
3. Open the project in Android Studio and wait for Gradle sync
4. No extra API configuration required
5. Click Build -> Make Project to complete building

## ▶️ Run Project
1. Connect an Android device or launch an emulator
2. Select app run configuration
3. Click the Run button (Shift+F10)
4. The application will be installed and launched automatically
5. Also can use the apk directly, the path is app/release/app-release.apk

 App Usage:
- Home: Browse GitHub trending repositories
- Search: Search repositories by programming language
- Detail: View repo information and create issues after login
- Profile: Show login guide for guest users; show user profile & personal repos for logged-in users

## 🧪 Test Guide
- Login state test: Profile page shows login guide for guests and user data for logged-in users
- UI state test: Correct display for loading, error, empty and success state
- Navigation test: All repository items can jump to detail page normally
- Logout test: Clear local data and reset to unlogged state after logout
- Network test: Disable network to trigger error view and retry button on all pages
- video presentation: [/video/视频演示.webm](/video/视频演示.webm)

