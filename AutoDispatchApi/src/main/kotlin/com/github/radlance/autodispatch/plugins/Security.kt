package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val tokenService by inject<TokenService>()
    val audience = environment.config.property("jwt.audience").getString()

    authentication {
        jwt {
            verifier(tokenService.verifyToken())
            validate { credential ->
                if (credential.payload.audience.contains(audience) && credential.payload.claims.contains("login")) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}
