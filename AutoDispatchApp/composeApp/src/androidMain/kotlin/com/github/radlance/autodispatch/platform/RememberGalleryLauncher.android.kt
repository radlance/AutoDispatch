package com.github.radlance.autodispatch.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberGalleryLauncher(
    onResult: (ByteArray?) -> Unit
): GalleryLauncher {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }

        scope.launch(Dispatchers.IO) {
            try {
                val processedBytes = context.processImageForAvatar(uri)

                withContext(Dispatchers.Main) {
                    onResult(processedBytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    return remember {
        object : GalleryLauncher {
            override fun pick() {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

private fun Context.processImageForAvatar(
    uri: Uri,
    targetSize: Int = 512,
    jpegQuality: Int = 85
): ByteArray {
    val contentResolver = contentResolver

    val originalBitmap = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it)
    } ?: error("Cannot decode bitmap")

    val orientation = contentResolver.openInputStream(uri)?.use { input ->
        ExifInterface(input).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } ?: ExifInterface.ORIENTATION_NORMAL

    val rotatedBitmap = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
        else -> originalBitmap
    }

    val size = minOf(rotatedBitmap.width, rotatedBitmap.height)
    val x = (rotatedBitmap.width - size) / 2
    val y = (rotatedBitmap.height - size) / 2

    val squareBitmap = Bitmap.createBitmap(
        rotatedBitmap,
        x,
        y,
        size,
        size
    )

    val scaledBitmap = squareBitmap.scale(targetSize, targetSize)

    val output = ByteArrayOutputStream()
    scaledBitmap.compress(
        Bitmap.CompressFormat.JPEG,
        jpegQuality,
        output
    )

    return output.toByteArray()
}

private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}