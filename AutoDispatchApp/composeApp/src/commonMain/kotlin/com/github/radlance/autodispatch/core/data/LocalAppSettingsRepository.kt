package com.github.radlance.autodispatch.core.data

import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.core.domain.AppSettingsRepository
import com.github.radlance.autodispatch.uikit.theme.AppSettings
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

class LocalAppSettingsRepository(
    private val dataStoreManager: DataStoreManager
) : AppSettingsRepository {

    override val appSettings: Flow<AppSettings> = dataStoreManager.appSettings

    override suspend fun updateThemeMode(mode: ThemeMode) {
        dataStoreManager.updateThemeMode(mode)
    }

    override suspend fun updateThemeAccent(accent: ThemeAccent) {
        dataStoreManager.updateThemeAccent(accent)
    }

    override suspend fun updateAmoledEnabled(enabled: Boolean) {
        dataStoreManager.updateAmoledEnabled(enabled)
    }
}