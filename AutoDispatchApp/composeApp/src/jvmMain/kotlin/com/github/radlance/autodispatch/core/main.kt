package com.github.radlance.autodispatch.core

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.radlance.autodispatch.core.presentation.App
import com.github.radlance.autodispatch.di.adminModule
import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.changeRequestModule
import com.github.radlance.autodispatch.di.changeUserModule
import com.github.radlance.autodispatch.di.commonModule
import com.github.radlance.autodispatch.di.controlPanelModule
import com.github.radlance.autodispatch.di.dataStoreModule
import com.github.radlance.autodispatch.di.driveRequestModule
import com.github.radlance.autodispatch.di.driverAssignmentModule
import com.github.radlance.autodispatch.di.driverHistoryModule
import com.github.radlance.autodispatch.di.driverModule
import com.github.radlance.autodispatch.di.driverVehicleModule
import com.github.radlance.autodispatch.di.navigationModule
import com.github.radlance.autodispatch.di.pointSelectionModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import com.github.radlance.autodispatch.di.settingsModule
import com.github.radlance.autodispatch.di.statisticsModule
import com.github.radlance.autodispatch.di.vehicleAssignmentModule
import com.github.radlance.autodispatch.di.vehicleModule
import com.github.radlance.autodispatch.di.vehicleUnassignmentModule
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
            vehicleModule,
            driverVehicleModule,
            vehicleUnassignmentModule,
            statisticsModule,
            settingsModule,
            adminModule,
            changeUserModule
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1100.dp, height = 750.dp),
        title = "АвтоЗаявка"
    ) {
        window.minimumSize = Dimension(1100, 750)
        App()
    }
}