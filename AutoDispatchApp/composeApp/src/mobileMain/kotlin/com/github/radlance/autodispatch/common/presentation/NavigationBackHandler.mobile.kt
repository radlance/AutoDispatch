package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler as AndroidxNavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable
actual fun NavigationBackHandler(
    enabled: Boolean,
    onBackCompleted: () -> Unit
) {
    val state = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None,
        backInfo = emptyList(),
        forwardInfo = emptyList()
    )

    AndroidxNavigationBackHandler(
        state = state,
        isBackEnabled = enabled,
        onBackCancelled = {},
        onBackCompleted = onBackCompleted
    )
}

