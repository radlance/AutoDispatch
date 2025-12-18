package com.github.radlance.autodispatch.core

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.changeRequestModule
import com.github.radlance.autodispatch.di.commonModule
import com.github.radlance.autodispatch.di.controlPanelModule
import com.github.radlance.autodispatch.di.dataStoreModule
import com.github.radlance.autodispatch.di.driveRequestModule
import com.github.radlance.autodispatch.di.driverAssignmentModule
import com.github.radlance.autodispatch.di.driverHistoryModule
import com.github.radlance.autodispatch.di.driverModule
import com.github.radlance.autodispatch.di.navigationModule
import com.github.radlance.autodispatch.di.pointSelectionModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import com.github.radlance.autodispatch.di.vehicleAssignmentModule
import com.github.radlance.autodispatch.di.vehicleModule
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
            controlPanelModule,
            requestModule,
            changeRequestModule,
            driverAssignmentModule,
            pointSelectionModule,
            driverModule,
            vehicleAssignmentModule,
            driverHistoryModule,
            driveRequestModule,
            vehicleModule
        )
    }
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "АвтоЗаявка"
    ) {
        window.minimumSize = Dimension(1100, 750)
        App()
    }
}