package com.example.githubclient.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(context: Context) {
    private val dataStore = context.dataStore
    private val TOKEN_KEY = stringPreferencesKey("github_token")

    val authTokenFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }

    suspend fun saveAuthToken(token: String) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[TOKEN_KEY] = token
            }
        }
    }

    suspend fun clearAuthToken() {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this.remove(TOKEN_KEY)
            }
        }
    }
}