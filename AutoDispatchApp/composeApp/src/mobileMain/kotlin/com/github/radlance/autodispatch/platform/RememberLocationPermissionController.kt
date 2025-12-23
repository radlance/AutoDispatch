package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController