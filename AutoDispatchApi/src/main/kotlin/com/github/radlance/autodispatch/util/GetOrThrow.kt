package com.github.radlance.autodispatch.util

import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.exception.UnauthorizedException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*

suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T {
    return runCatching { receive<T>() }.getOrElse { throw MissingCredentialException() }
}

inline fun <reified T : Any> ApplicationCall.claimByNameOrElse(name: String, action: () -> Nothing): T {
    val principal = principal<JWTPrincipal>()
    return principal?.getClaim(name, T::class) ?: action()
}


inline fun <reified T : Any> ApplicationCall.claimByNameOrUnauthorized(name: String): T {
    return claimByNameOrElse<T>(name = name) {
        throw UnauthorizedException()
    }
}