package com.github.radlance.autodispatch.platform

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
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
actual fun rememberPhotoPermissionController(
    onPermissionResult: (Boolean) -> Unit
): PermissionController {

    val context = LocalContext.current
    val activity = context as Activity

    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

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
            val permanentlyDenied = permissions.all { permission ->
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
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
                val isGranted = permissions.any { permission ->
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                }

                if (isGranted) {
                    onPermissionResult(true)
                    return
                }

                launcher.launch(permissions)
                firstRequestDone = true
            }

            override fun hasPermission(): Boolean {
                return permissions.any { permission ->
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }
}
