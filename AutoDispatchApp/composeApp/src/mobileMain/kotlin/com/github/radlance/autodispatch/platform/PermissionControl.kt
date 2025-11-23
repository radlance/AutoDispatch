package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): LocationPermissionController

interface LocationPermissionController {

    fun askPermission()

    fun hasPermission(): Boolean
}