package com.github.radlance.autodispatch.core.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.github.radlance.autodispatch.navigation.core.NavGraph
import com.github.radlance.autodispatch.uikit.theme.AppTheme
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(viewModel: AppSettingsViewModel = koinViewModel()) {
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()

    val isDark = when (appSettings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    AppTheme(
        isDark = isDark,
        amoled = appSettings.amoledEnabled,
        accent = appSettings.themeAccent
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            NavGraph(rememberNavController())
        }
    }
}