package com.github.radlance.autodispatch.uikit.theme

import androidx.compose.ui.graphics.Color
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.theme_color_blue
import autodispatch.composeapp.generated.resources.theme_color_default
import autodispatch.composeapp.generated.resources.theme_color_green
import autodispatch.composeapp.generated.resources.theme_color_orange
import autodispatch.composeapp.generated.resources.theme_color_purple
import autodispatch.composeapp.generated.resources.theme_color_red
import autodispatch.composeapp.generated.resources.theme_color_teal
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class ThemeAccent(
    val labelRes: StringResource,
    val seedColor: Color
) {
    DEFAULT(Res.string.theme_color_default, Color(0xFF4355B9)),
    BLUE(Res.string.theme_color_blue, Color(0xFF1F6FEB)),
    GREEN(Res.string.theme_color_green, Color(0xFF2E7D32)),
    ORANGE(Res.string.theme_color_orange, Color(0xFFEF6C00)),
    RED(Res.string.theme_color_red, Color(0xFFB3261E)),
    PURPLE(Res.string.theme_color_purple, Color(0xFF7B4AE2)),
    TEAL(Res.string.theme_color_teal, Color(0xFF00897B))
}