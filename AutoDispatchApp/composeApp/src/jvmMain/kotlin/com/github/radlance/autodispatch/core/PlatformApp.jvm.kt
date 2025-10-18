package com.github.radlance.autodispatch.core

import androidx.compose.runtime.Composable
import com.github.radlance.autodispatch.presentation.auth.SignInScreen

@Composable
actual fun PlatformApp() {
    SignInScreen()
}