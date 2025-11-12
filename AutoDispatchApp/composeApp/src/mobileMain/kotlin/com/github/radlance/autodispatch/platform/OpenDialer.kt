package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable

expect fun openDialer(phoneNumber: String, context: Any?)

@Composable
expect fun getPlatformContext(): Any?