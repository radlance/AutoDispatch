package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.repository.*
import com.github.radlance.autodispatch.security.hashing.HashingService
import com.github.radlance.autodispatch.security.hashing.SHA256HashingService
import com.github.radlance.autodispatch.security.token.TokenConfig
import com.github.radlance.autodispatch.security.token.TokenService
import com.github.radlance.autodispatch.service.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Application.authModule
    get() = module {
        singleOf(::SHA256HashingService) bind HashingService::class
        singleOf(::AuthRepository)
        singleOf(::TokenService)
        singleOf(::AuthService)
        single {
            TokenConfig(
                issuer = environment.config.property("jwt.issuer").getString(),
                audience = environment.config.property("jwt.audience").getString(),
                secret = environment.config.property("jwt.secret").getString(),
                expiresIn = environment.config.property("jwt.expiration").getAs(),
                totalExpiresIn = environment.config.property("jwt.total-expiration").getAs()
            )
        }
    }

val profileModule
    get() = module {
        singleOf(::ProfileRepository)
    }

val Application.requestModule
    get() = module {
        singleOf(::RequestRepository)
        single { KtorRabbitNotificationPublisher(this@requestModule) }.bind<NotificationPublisher>()
        singleOf(::RequestService)
    }

val deliveryModule
    get() = module {
        singleOf(::DeliveryRepository)
    }

val documentModule
    get() = module {
        singleOf(::DocumentsRepository)
    }

val driverModule
    get() = module {
        singleOf(::DriverRepository)
        singleOf(::MailService)
    }

val vehicleModule
    get() = module {
        singleOf(::VehicleRepository)
    }

val statisticsModule
    get() = module {
        singleOf(::StatisticsRepository)
    }