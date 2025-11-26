package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraLauncher(
    onResult: (ByteArray?) -> Unit
): CameraLauncher

interface CameraLauncher {
    fun capture()
}