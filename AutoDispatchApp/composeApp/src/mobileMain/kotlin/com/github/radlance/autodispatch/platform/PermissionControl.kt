package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController

interface PermissionController {

    fun askPermission()

    fun hasPermission(): Boolean
}

expect fun openAppSettings(context: Any?)

@Composable
expect fun createCameraPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController