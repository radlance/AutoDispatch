package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun NavigationBackHandler(
    enabled: Boolean = true,
    onBackCompleted: () -> Unit
) 
