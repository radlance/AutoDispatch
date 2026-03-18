@file:Suppress("DEPRECATION")
@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.backhandler.BackHandler as DeprecatedBackHandler

@Composable
fun AppBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    DeprecatedBackHandler(enabled = enabled, onBack = onBack)
}
