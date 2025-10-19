package com.github.radlance.autodispatch.core

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.radlance.autodispatch.di.initKoin
import java.awt.Dimension

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1100.dp, height = 750.dp),
        title = "AutoDispatch",
    ) {
        window.minimumSize = Dimension(750, 600)
        App()
    }
}