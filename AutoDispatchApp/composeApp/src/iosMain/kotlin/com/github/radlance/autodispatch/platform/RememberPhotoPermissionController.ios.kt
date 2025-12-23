package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberPhotoPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController {
    return remember { IosGalleryPermissionController(onPermissionResult) }
}

private class IosGalleryPermissionController(
    private val onResult: (Boolean) -> Unit
) : PermissionController {

    override fun askPermission() {
        val status = PHPhotoLibrary.authorizationStatus()

        when (status) {
            PHAuthorizationStatusNotDetermined -> {
                PHPhotoLibrary.requestAuthorization { newStatus ->
                    dispatch_async(dispatch_get_main_queue()) {
                        onResult(isGranted(newStatus))
                    }
                }
            }

            else -> {
                onResult(hasPermission())
            }
        }
    }

    override fun hasPermission(): Boolean {
        val status = PHPhotoLibrary.authorizationStatus()
        return isGranted(status)
    }

    private fun isGranted(status: PHAuthorizationStatus): Boolean {
        return status == PHAuthorizationStatusAuthorized ||
                status == PHAuthorizationStatusLimited
    }
}
