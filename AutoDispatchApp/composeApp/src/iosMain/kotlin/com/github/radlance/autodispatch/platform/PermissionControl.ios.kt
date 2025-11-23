package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

@Composable
actual fun createLocationPermissionController(onPermissionResult: (Boolean) -> Unit): LocationPermissionController {
    TODO("Not yet implemented")
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
        } else checkStatus()
    }

    override fun checkStatus() {
        val status = locationManager.authorizationStatus
        val isGranted = (status == kCLAuthorizationStatusAuthorizedAlways ||
                status == kCLAuthorizationStatusAuthorizedWhenInUse)

        onResult(isGranted)
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        checkStatus()
    }
}