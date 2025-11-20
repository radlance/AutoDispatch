package com.github.radlance.autodispatch.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

actual fun openMap(address: String, context: Any?) {
    val ctx = context as? Context ?: return
    val uri = "geo:0,0?q=${Uri.encode(address)}".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)

    val chooser = Intent.createChooser(intent, "Открыть через")
    ctx.startActivity(chooser)
}