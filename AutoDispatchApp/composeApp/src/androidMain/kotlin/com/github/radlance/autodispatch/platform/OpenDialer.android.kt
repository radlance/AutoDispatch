package com.github.radlance.autodispatch.platform

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

actual fun openDialer(phoneNumber: String, context: Any?) {
    val ctx = context as? Context ?: return
    val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
    ctx.startActivity(intent)
}

@Composable
actual fun getPlatformContext(): Any? = LocalContext.current