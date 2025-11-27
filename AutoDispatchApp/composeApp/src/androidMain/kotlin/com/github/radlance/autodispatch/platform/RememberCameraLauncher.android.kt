package com.github.radlance.autodispatch.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher {
    val context = LocalContext.current

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempPhotoUri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(tempPhotoUri!!)
                val bytes = inputStream?.use { it.readBytes() }
                onResult(bytes)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    return remember {
        object : CameraLauncher {
            override fun capture() {
                val uri = createTempPictureUri(context)
                tempPhotoUri = uri
                launcher.launch(uri)
            }
        }
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "picture_${System.currentTimeMillis()}",
        ".jpg",
        File(context.cacheDir, "images").apply { mkdirs() } // Создаем папку images в кэше, если нет
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )
}