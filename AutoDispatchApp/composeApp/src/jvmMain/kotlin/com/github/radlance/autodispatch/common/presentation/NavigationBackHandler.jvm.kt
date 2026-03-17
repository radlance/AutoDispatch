package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun NavigationBackHandler(
    enabled: Boolean,
    onBackCompleted: () -> Unit
) {
    BackHandler(enabled = enabled, onBack = onBackCompleted)
}

