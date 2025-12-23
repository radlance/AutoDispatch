package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun createCameraPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController