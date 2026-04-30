package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFileSaver(): FileSaver

interface FileSaver {
    fun saveAndOpen(fileName: String, bytes: ByteArray)
}