package com.github.radlance.autodispatch.util

import com.github.radlance.autodispatch.exception.MissingCredentialException
import io.ktor.server.application.*
import io.ktor.server.request.*

suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T {
    return runCatching { receive<T>() }.getOrElse { throw MissingCredentialException() }
}