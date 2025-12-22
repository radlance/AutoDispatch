package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

@Composable
actual fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController {
    return remember { IosLocationPermissionController(onPermissionResult) }
}

private class IosLocationPermissionController(
    private val onResult: (Boolean) -> Unit
) : PermissionController {

    private val manager = CLLocationManager()
    private var authDelegate: AuthorizationDelegate? = null

    override fun askPermission() {
        authDelegate = AuthorizationDelegate { granted ->
            onResult(granted)
        }
        manager.delegate = authDelegate

        val status = CLLocationManager.authorizationStatus()
        if (status == kCLAuthorizationStatusNotDetermined) {
            manager.requestWhenInUseAuthorization()
        } else {
            onResult(hasPermission())
        }
    }

    override fun hasPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }

    private class AuthorizationDelegate(
        private val onAuthChanged: (Boolean) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            val status = manager.authorizationStatus()
            val granted = status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                    status == kCLAuthorizationStatusAuthorizedAlways
            onAuthChanged(granted)
        }
    }
}
