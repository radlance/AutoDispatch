package com.github.radlance.autodispatch.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataStoreManager {

    val token: Flow<String?>

    suspend fun saveToken(token: String)

    suspend fun deleteToken()

    val sessionExpired: Flow<Boolean>

    suspend fun saveSessionExpired(expired: Boolean)
}

internal class BaseDataStoreManager(
    private val dataStore: DataStore<Preferences>
) : DataStoreManager {
    override suspend fun saveToken(token: String) {
        dataStore.edit { settings -> settings[KEY_TOKEN] = token }
    }

    override val token: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    override suspend fun deleteToken() {
        dataStore.edit { preferences -> preferences.remove(KEY_TOKEN) }
    }

    override val sessionExpired: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SESSION_EXPIRED] ?: false
    }

    override suspend fun saveSessionExpired(expired: Boolean) {
        dataStore.edit { settings -> settings[KEY_SESSION_EXPIRED] = expired }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_SESSION_EXPIRED = booleanPreferencesKey("session_expired")
    }
}