package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun MapPoint(address: String, onDismiss: () -> Unit)