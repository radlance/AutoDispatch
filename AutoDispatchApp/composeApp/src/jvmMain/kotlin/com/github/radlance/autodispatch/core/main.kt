package com.github.radlance.autodispatch.core

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.commonModule
import com.github.radlance.autodispatch.di.controlPanelModule
import com.github.radlance.autodispatch.di.dataStoreModule
import com.github.radlance.autodispatch.di.navigationModule
import com.github.radlance.autodispatch.di.profileModule
import org.koin.core.context.startKoin
import java.awt.Dimension

fun main() = application {
    startKoin {
        modules(
            dataStoreModule,
            commonModule,
            navigationModule,
            authModule,
            profileModule,
            controlPanelModule
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1100.dp, height = 750.dp),
        title = "АвтоЗаявка",
    ) {
        window.minimumSize = Dimension(750, 600)
        App()
    }
}