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
): LocationPermissionController {
    return remember { IosPermissionController(onPermissionResult) }
}

private class IosPermissionController(
    private val onResult: (Boolean) -> Unit
) : LocationPermissionController {

    private val locationManager = CLLocationManager()

    override fun askPermission() {
        locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                onResult(hasPermission())
            }
        }

        val status = locationManager.authorizationStatus()

        if (status == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization()
        } else {
            onResult(hasPermission())
        }
    }

    override fun hasPermission(): Boolean {
        val status = locationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }
}
