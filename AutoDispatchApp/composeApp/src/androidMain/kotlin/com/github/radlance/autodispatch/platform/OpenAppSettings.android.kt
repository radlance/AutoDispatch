package com.github.radlance.autodispatch.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

actual fun openAppSettings(context: Any?) {
    val ctx = context as? Context ?: return
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", ctx.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}