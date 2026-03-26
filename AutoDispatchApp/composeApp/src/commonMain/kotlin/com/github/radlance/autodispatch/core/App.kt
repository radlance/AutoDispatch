package com.github.radlance.autodispatch.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.navigation.core.NavGraph
import com.github.radlance.autodispatch.uikit.theme.AppTheme
import com.github.radlance.autodispatch.uikit.theme.ThemeAccent
import com.github.radlance.autodispatch.uikit.theme.ThemeMode
import org.koin.compose.koinInject
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun App() {
    val dataStoreManager = koinInject<DataStoreManager>()
    val themeMode by dataStoreManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val themeAccent by dataStoreManager.themeAccent.collectAsState(initial = ThemeAccent.DEFAULT)
    val amoledEnabled by dataStoreManager.amoledEnabled.collectAsState(initial = false)
    val systemDark = isSystemInDarkTheme()

    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    AppTheme(
        isDark = isDark,
        amoled = amoledEnabled,
        accent = themeAccent
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            NavGraph(rememberNavController())
        }
    }
}