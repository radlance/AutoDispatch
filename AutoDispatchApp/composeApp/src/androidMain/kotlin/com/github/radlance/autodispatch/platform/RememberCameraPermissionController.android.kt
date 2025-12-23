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
actual fun createCameraPermissionController(onPermissionResult: (Boolean) -> Unit): PermissionController {
    val context = LocalContext.current
    val activity = context as Activity

    var firstRequestDone by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionResult(true)
            return@rememberLauncherForActivityResult
        }

        if (firstRequestDone) {
            val permanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )

            if (permanentlyDenied) {
                onPermissionResult(false)
                return@rememberLauncherForActivityResult
            }
        }

        onPermissionResult(false)
    }

    return remember {
        object : PermissionController {
            override fun askPermission() {
                val isGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (isGranted) {
                    onPermissionResult(true)
                    return
                }

                launcher.launch(Manifest.permission.CAMERA)
                firstRequestDone = true
            }

            override fun hasPermission(): Boolean {
                return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}