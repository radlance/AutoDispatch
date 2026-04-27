package com.github.radlance.autodispatch.core.domain

import com.github.radlance.autodispatch.uikit.theme.AppSettings
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {

    val appSettings: Flow<AppSettings>

    suspend fun updateThemeMode(mode: ThemeMode)

    suspend fun updateThemeAccent(accent: ThemeAccent)

    suspend fun updateAmoledEnabled(enabled: Boolean)
}