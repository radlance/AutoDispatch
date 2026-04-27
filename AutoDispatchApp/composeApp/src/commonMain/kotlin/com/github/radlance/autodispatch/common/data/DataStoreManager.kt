package com.github.radlance.autodispatch.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.radlance.autodispatch.uikit.theme.AppSettings
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataStoreManager {

    val token: Flow<String?>

    suspend fun saveToken(token: String)

    suspend fun deleteToken()

    val userRoleId: Flow<Int?>

    suspend fun saveUserRoleId(roleId: Int)

    suspend fun deleteUserRoleId()

    val sessionExpired: Flow<Boolean>

    suspend fun saveSessionExpired(expired: Boolean)

    val locationPermissionAsked: Flow<Boolean>

    suspend fun setLocationPermissionAsked(asked: Boolean)

    val appSettings: Flow<AppSettings>

    suspend fun updateThemeMode(mode: ThemeMode)

    suspend fun updateThemeAccent(accent: ThemeAccent)

    suspend fun updateAmoledEnabled(enabled: Boolean)
}

internal class BaseDataStoreManager(
    private val dataStore: DataStore<Preferences>
) : DataStoreManager {
    override suspend fun saveToken(token: String) {
        dataStore.edit { prefs -> prefs[KEY_TOKEN] = token }
    }

    override val token: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    override suspend fun deleteToken() {
        dataStore.edit { prefs -> prefs.remove(KEY_TOKEN) }
    }

    override val userRoleId: Flow<Int?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_ROLE_ID]
    }

    override suspend fun saveUserRoleId(roleId: Int) {
        dataStore.edit { prefs -> prefs[KEY_USER_ROLE_ID] = roleId }
    }

    override suspend fun deleteUserRoleId() {
        dataStore.edit { prefs -> prefs.remove(KEY_USER_ROLE_ID) }
    }

    override val sessionExpired: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_SESSION_EXPIRED] ?: false
    }

    override suspend fun saveSessionExpired(expired: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_SESSION_EXPIRED] = expired }
    }

    override val locationPermissionAsked: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_ASKED] ?: false
    }

    override suspend fun setLocationPermissionAsked(asked: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_LOCATION_ASKED] = asked }
    }

    override val appSettings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = ThemeMode.entries.firstOrNull { it.name == prefs[KEY_THEME_MODE] }
                ?: ThemeMode.SYSTEM,
            themeAccent = ThemeAccent.entries.firstOrNull { it.name == prefs[KEY_THEME_ACCENT] }
                ?: ThemeAccent.DEFAULT,
            amoledEnabled = prefs[KEY_AMOLED_ENABLED] ?: false
        )
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode.name }
    }

    override suspend fun updateThemeAccent(accent: ThemeAccent) {
        dataStore.edit { prefs -> prefs[KEY_THEME_ACCENT] = accent.name }
    }

    override suspend fun updateAmoledEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_AMOLED_ENABLED] = enabled }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ROLE_ID = intPreferencesKey("user_role_id")
        val KEY_SESSION_EXPIRED = booleanPreferencesKey("session_expired")
        val KEY_LOCATION_ASKED = booleanPreferencesKey("location_permission_asked")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_THEME_ACCENT = stringPreferencesKey("theme_accent")
        val KEY_AMOLED_ENABLED = booleanPreferencesKey("amoled_enabled")
    }
}