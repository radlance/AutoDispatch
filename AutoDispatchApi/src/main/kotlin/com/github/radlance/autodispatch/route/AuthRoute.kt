package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.auth.LoginUser
import com.github.radlance.autodispatch.domain.auth.RefreshTokenRequest
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.service.AuthService
import com.github.radlance.autodispatch.util.receiveOrThrow
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.auth(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receiveOrThrow<RegisterUser>()
            val createdUser: User = authService.register(user = request)
            call.respond(createdUser)
        }

        post("/login") {
            val request = call.receiveOrThrow<LoginUser>()
            val tokens = authService.login(user = request)
            call.respond(HttpStatusCode.OK, tokens)
        }

        post("/refresh-token") {
            val request = call.receiveOrThrow<RefreshTokenRequest>()
            try {
                val tokens = authService.refreshToken(request)
                call.respond(HttpStatusCode.OK, tokens)
            } catch (_: MissingCredentialException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Session expired or invalid"))
            }
        }
    }
}