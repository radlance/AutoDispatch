package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.deliveryModule
import com.github.radlance.autodispatch.di.documentModule
import com.github.radlance.autodispatch.di.driverModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import com.github.radlance.autodispatch.di.statisticsModule
import com.github.radlance.autodispatch.di.vehicleModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(
            authModule,
            profileModule,
            requestModule,
            deliveryModule,
            documentModule,
            driverModule,
            vehicleModule,
            statisticsModule
        )
    }
}
