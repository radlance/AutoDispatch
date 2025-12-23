package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPhotoPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController