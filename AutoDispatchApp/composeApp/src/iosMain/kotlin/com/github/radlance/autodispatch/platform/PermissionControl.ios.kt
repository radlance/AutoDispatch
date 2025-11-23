package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

// TODO проверить ios + plist, сделатть открытие настроек
@Composable
actual fun createLocationPermissionController(onPermissionResult: (Boolean) -> Unit): LocationPermissionController {
    return remember { IosPermissionController(onPermissionResult) }
}

private class IosPermissionController(
    val onResult: (Boolean) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol, LocationPermissionController {
    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    override fun askPermission() {
        val status = locationManager.authorizationStatus
        if (status == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization()
        } else onResult(hasPermission())
    }

    override fun hasPermission(): Boolean {
        val status = locationManager.authorizationStatus
        return (status == kCLAuthorizationStatusAuthorizedAlways ||
                status == kCLAuthorizationStatusAuthorizedWhenInUse)
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        onResult(hasPermission())
    }
}