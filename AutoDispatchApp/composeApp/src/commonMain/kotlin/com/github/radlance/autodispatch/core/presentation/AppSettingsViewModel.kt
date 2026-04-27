package com.github.radlance.autodispatch.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.core.domain.AppSettingsRepository
import com.github.radlance.autodispatch.uikit.theme.AppSettings
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = appSettingsRepository.appSettings.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            appSettingsRepository.updateThemeMode(mode)
        }
    }

    fun updateThemeAccent(accent: ThemeAccent) {
        viewModelScope.launch {
            appSettingsRepository.updateThemeAccent(accent)
        }
    }

    fun updateAmoledEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.updateAmoledEnabled(enabled)
        }
    }
}