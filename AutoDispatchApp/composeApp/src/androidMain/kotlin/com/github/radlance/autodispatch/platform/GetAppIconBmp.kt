package com.github.radlance.autodispatch.platform

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

internal fun PackageManager.getAppIconBmp(packageName: String): ImageBitmap {
    val drawable = getApplicationIcon(packageName)
    if (drawable is BitmapDrawable) {
        return drawable.bitmap.asImageBitmap()
    }

    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

internal class AndroidMapApp(
    val name: String,
    val intent: Intent,
    val icon: ImageBitmap
)
