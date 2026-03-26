package com.github.radlance.autodispatch.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataStoreManager {

    val token: Flow<String?>

    suspend fun saveToken(token: String)

    suspend fun deleteToken()

    val sessionExpired: Flow<Boolean>

    suspend fun saveSessionExpired(expired: Boolean)

    val locationPermissionAsked: Flow<Boolean>

    suspend fun setLocationPermissionAsked(asked: Boolean)

    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)

    val themeAccent: Flow<ThemeAccent>

    suspend fun setThemeAccent(accent: ThemeAccent)

    val amoledEnabled: Flow<Boolean>

    suspend fun setAmoledEnabled(enabled: Boolean)
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

    override val locationPermissionAsked: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_LOCATION_ASKED] ?: false
    }

    override suspend fun setLocationPermissionAsked(asked: Boolean) {
        dataStore.edit { settings -> settings[KEY_LOCATION_ASKED] = asked }
    }

    override val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.entries.firstOrNull { it.name == preferences[KEY_THEME_MODE] }
            ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { settings -> settings[KEY_THEME_MODE] = mode.name }
    }

    override val themeAccent: Flow<ThemeAccent> = dataStore.data.map { preferences ->
        ThemeAccent.entries.firstOrNull { it.name == preferences[KEY_THEME_ACCENT] }
            ?: ThemeAccent.DEFAULT
    }

    override suspend fun setThemeAccent(accent: ThemeAccent) {
        dataStore.edit { settings -> settings[KEY_THEME_ACCENT] = accent.name }
    }

    override val amoledEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_AMOLED_ENABLED] ?: false
    }

    override suspend fun setAmoledEnabled(enabled: Boolean) {
        dataStore.edit { settings -> settings[KEY_AMOLED_ENABLED] = enabled }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_SESSION_EXPIRED = booleanPreferencesKey("session_expired")
        private val KEY_LOCATION_ASKED = booleanPreferencesKey("location_permission_asked")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_THEME_ACCENT = stringPreferencesKey("theme_accent")
        private val KEY_AMOLED_ENABLED = booleanPreferencesKey("amoled_enabled")
    }
}