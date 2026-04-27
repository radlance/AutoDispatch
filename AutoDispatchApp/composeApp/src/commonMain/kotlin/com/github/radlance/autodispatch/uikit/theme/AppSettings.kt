package com.github.radlance.autodispatch.uikit.theme

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeAccent: ThemeAccent = ThemeAccent.DEFAULT,
    val amoledEnabled: Boolean = false
)