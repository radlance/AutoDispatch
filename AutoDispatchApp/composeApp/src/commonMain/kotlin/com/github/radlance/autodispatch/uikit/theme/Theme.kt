package com.github.radlance.autodispatch.uikit.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun AppTheme(
    isDark: Boolean,
    amoled: Boolean = false,
    accent: ThemeAccent = ThemeAccent.DEFAULT,
    content: @Composable () -> Unit
) {

    var colorScheme = rememberDynamicColorScheme(
        seedColor = accent.seedColor,
        isDark = isDark,
        isAmoled = isDark && amoled,
        style = PaletteStyle.TonalSpot
    )

    if (!isDark) {
        colorScheme = colorScheme.copy(
            background = Color(0xFFFEFBFF),
            surface = Color(0xFFFEFBFF),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF7F5FA)
        )
    } else if (!amoled) {
        colorScheme = colorScheme.copy(
            background = Color(0xFF1B1A1F),
            surface = Color(0xFF1B1A1F),
            surfaceContainerLowest = Color(0xFF14141A),
            surfaceContainerLow = Color(0xFF1D1D23)
        )
    }

    val statusPalette = if (isDark) DarkStatusPalette else LightStatusPalette

    CompositionLocalProvider(LocalStatusPalette provides statusPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
