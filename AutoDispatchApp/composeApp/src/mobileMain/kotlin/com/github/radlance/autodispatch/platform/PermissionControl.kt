package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import com.github.radlance.autodispatch.delivery.route.domain.Location

@Composable
expect fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController

interface PermissionController {

    fun askPermission()

    fun hasPermission(): Boolean
}

expect fun openAppSettings(context: Any?)

expect suspend fun getCurrentLocation(context: Any?): Location?

@Composable
expect fun createCameraPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController