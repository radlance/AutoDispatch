package com.github.radlance.autodispatch

import androidx.compose.ui.window.ComposeUIViewController
import com.github.radlance.autodispatch.core.App
import com.github.radlance.autodispatch.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}