package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryLauncher(
    onResult: (ByteArray?) -> Unit
): GalleryLauncher

interface GalleryLauncher {
    fun pick()
}
