package com.github.radlance.autodispatch.platform

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
actual fun createLocationPermissionController(
    onPermissionResult: (Boolean) -> Unit
): LocationPermissionController {

    val context = LocalContext.current
    val activity = context as Activity

    var firstRequestDone by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        val granted = result.values.any { it }

        if (granted) {
            onPermissionResult(true)
            return@rememberLauncherForActivityResult
        }

        if (firstRequestDone) {
            val permanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permanentlyDenied) {
                onPermissionResult(false)
                return@rememberLauncherForActivityResult
            }
        }

        onPermissionResult(false)
    }

    return remember {
        object : LocationPermissionController {

            override fun askPermission() {
                val isGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                            PackageManager.PERMISSION_GRANTED

                if (isGranted) {
                    onPermissionResult(true)
                    return
                }

                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                firstRequestDone = true
            }

            override fun hasPermission(): Boolean {
                val isGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                            PackageManager.PERMISSION_GRANTED

                return isGranted
            }
        }
    }
}