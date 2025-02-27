package com.valokafor.duckit.api.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")

class AuthDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val authToken = dataStore.data.map { preferences ->
        preferences[KEY_AUTH_TOKEN] ?: ""
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    suspend fun removeAuthToken(){
        dataStore.edit { preferences ->
            preferences.remove(KEY_AUTH_TOKEN)
        }
    }
}