package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

@Composable
expect fun MapRouteDialog(lat: Double, lon: Double, onDismiss: () -> Unit)
