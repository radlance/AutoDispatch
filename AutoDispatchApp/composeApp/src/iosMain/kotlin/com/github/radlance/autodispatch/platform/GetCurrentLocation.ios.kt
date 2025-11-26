package com.github.radlance.autodispatch.platform

import com.github.radlance.autodispatch.delivery.route.domain.Location
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

private object IOSLocationHolder {
    val manager: CLLocationManager = CLLocationManager()
    var delegate: IOSLocationDelegate? = null

    var isUpdating: Boolean = false
}

private class IOSLocationDelegate(
    private val onResult: (Location?) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus()
        if (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
            status == kCLAuthorizationStatusAuthorizedAlways
        ) {
            manager.startUpdatingLocation()
            IOSLocationHolder.isUpdating = true
        } else if (status != kCLAuthorizationStatusNotDetermined) {
            onResult(null)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>
    ) {
        val last = didUpdateLocations.lastOrNull() as? CLLocation
        if (last != null) {
            last.coordinate.useContents {
                onResult(Location(lat = latitude, lon = longitude))
            }
        } else {
            onResult(null)
        }

        manager.stopUpdatingLocation()
        IOSLocationHolder.isUpdating = false
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: NSError
    ) {
        onResult(null)
        manager.stopUpdatingLocation()
        IOSLocationHolder.isUpdating = false
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getCurrentLocation(context: Any?): Location? = withContext(Dispatchers.Main) {

    suspendCancellableCoroutine { cont ->
        val manager = IOSLocationHolder.manager

        val delegate = IOSLocationDelegate { location ->
            if (cont.isActive) {
                cont.resume(location)
            }
            IOSLocationHolder.delegate = null
            manager.delegate = null
        }

        IOSLocationHolder.delegate = delegate
        manager.delegate = delegate

        cont.invokeOnCancellation {
            manager.stopUpdatingLocation()
            IOSLocationHolder.delegate = null
            manager.delegate = null
        }

        val status = CLLocationManager.authorizationStatus()

        when (status) {
            kCLAuthorizationStatusNotDetermined -> {
                manager.requestWhenInUseAuthorization()
            }

            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                manager.startUpdatingLocation()
            }

            else -> {
                if (cont.isActive) cont.resume(null)
                IOSLocationHolder.delegate = null
            }
        }
    }
}