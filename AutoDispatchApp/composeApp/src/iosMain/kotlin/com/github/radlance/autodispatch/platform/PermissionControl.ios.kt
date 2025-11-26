package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.radlance.autodispatch.delivery.route.domain.Location
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

@Composable
actual fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): LocationPermissionController {
    return remember { IosPermissionController(onPermissionResult) }
}

private class IosPermissionController(
    private val onResult: (Boolean) -> Unit
) : LocationPermissionController {

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

actual fun openAppSettings(context: Any?) {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openUrlSimple(url)
    }
}

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

@Composable
actual fun createCameraPermissionController(onPermissionResult: (Boolean) -> Unit): CameraPermissionController {
    return remember { IosCameraPermissionController(onPermissionResult) }
}

private class IosCameraPermissionController(
    private val onResult: (Boolean) -> Unit
) : CameraPermissionController {

    override fun askPermission() {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

        when (status) {
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    dispatch_async(dispatch_get_main_queue()) {
                        onResult(granted)
                    }
                }
            }
            AVAuthorizationStatusAuthorized -> {
                onResult(true)
            }
            else -> {
                onResult(false)
            }
        }
    }

    override fun hasPermission(): Boolean {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status == AVAuthorizationStatusAuthorized
    }
}