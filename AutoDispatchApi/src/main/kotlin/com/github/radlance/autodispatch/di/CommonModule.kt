package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.repository.AuthRepository
import com.github.radlance.autodispatch.security.hashing.HashingService
import com.github.radlance.autodispatch.security.hashing.SHA256HashingService
import com.github.radlance.autodispatch.security.token.TokenConfig
import com.github.radlance.autodispatch.security.token.TokenService
import com.github.radlance.autodispatch.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Application.authModule
    get() = module {
        singleOf(::SHA256HashingService) bind HashingService::class
        single { AuthRepository() }
        single { TokenService(get()) }
        single {
            AuthService(
                authRepository = get(),
                hashingService = get(),
                tokenService = get()
            )
        }
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