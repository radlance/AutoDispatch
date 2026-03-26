package com.github.radlance.autodispatch.uikit.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StatusPalette(
    val neutralBg: Color,
    val neutralText: Color,
    val infoBg: Color,
    val infoText: Color,
    val progressBg: Color,
    val progressText: Color,
    val reviewBg: Color,
    val reviewText: Color,
    val successBg: Color,
    val successText: Color,
    val warningBg: Color,
    val warningText: Color,
    val errorBg: Color,
    val errorText: Color
)

val LightStatusPalette = StatusPalette(
    neutralBg = Color(0xFFECEFF1),
    neutralText = Color(0xFF455A64),
    infoBg = Color(0xFFE3E8F5),
    infoText = Color(0xFF3E4A6E),
    progressBg = Color(0xFFCFE1FF),
    progressText = Color(0xFF0A37A6),
    reviewBg = Color(0xFFE6E0FF),
    reviewText = Color(0xFF2B1A5E),
    successBg = Color(0xFFD7F5D0),
    successText = Color(0xFF0F4A1B),
    warningBg = Color(0xFFFFE7C2),
    warningText = Color(0xFF6B3D00),
    errorBg = Color(0xFFFFDAD6),
    errorText = Color(0xFF8C1D18)
)

val DarkStatusPalette = StatusPalette(
    neutralBg = Color(0xFF2A2F33),
    neutralText = Color(0xFFC9D1D9),
    infoBg = Color(0xFF2B3546),
    infoText = Color(0xFFC5CFDA),
    progressBg = Color(0xFF1B2F5A),
    progressText = Color(0xFFA9C7FF),
    reviewBg = Color(0xFF2B2357),
    reviewText = Color(0xFFD7CFFF),
    successBg = Color(0xFF1B3B24),
    successText = Color(0xFFA6E6B1),
    warningBg = Color(0xFF4A330F),
    warningText = Color(0xFFFFD69E),
    errorBg = Color(0xFF4B1C1C),
    errorText = Color(0xFFFFB3AE)
)

val LocalStatusPalette = staticCompositionLocalOf { LightStatusPalette }

val MaterialTheme.statusPalette: StatusPalette
    @Composable get() = LocalStatusPalette.current
