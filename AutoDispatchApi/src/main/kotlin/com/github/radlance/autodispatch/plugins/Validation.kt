package com.github.radlance.autodispatch.plugins

import com.auth0.jwt.exceptions.JWTVerificationException
import com.github.radlance.autodispatch.domain.auth.LoginUser
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.exception.DeliveryForbiddenException
import com.github.radlance.autodispatch.exception.DeliveryNotFoundException
import com.github.radlance.autodispatch.exception.DeliveryStateException
import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.exception.NoAccessException
import com.github.radlance.autodispatch.exception.UnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<RegisterUser> { user ->
            when {
                user.login.trim().isBlank() -> {
                    ValidationResult.Invalid("Invalid lgoin format")
                }

                user.fullName.trim().isBlank() -> {
                    ValidationResult.Invalid("Username should not be blank")
                }

                user.password.length !in (8..50) -> {
                    ValidationResult.Invalid("Password should be of min 8 and max 50 character in length")
                }

                else -> ValidationResult.Valid
            }
        }

        validate<LoginUser> { user ->
            when {
                user.login.trim().isBlank() -> {
                    ValidationResult.Invalid("Invalid login format")
                }

                user.password.trim().length !in (8..50) -> {
                    ValidationResult.Invalid("Password should be of min 8 and max 50 character in length")
                }

                else -> ValidationResult.Valid
            }
        }
    }

    install(StatusPages) {
        exception<DeliveryStateException> { call, cause ->
            call.respondText(status = HttpStatusCode.Conflict, text = cause.message)
        }

        exception<DeliveryNotFoundException> { call, cause ->
            call.respondText(status = HttpStatusCode.NotFound, text = cause.message)
        }

        exception<DeliveryForbiddenException> { call, cause ->
            call.respondText(status = HttpStatusCode.Forbidden, text = cause.message)
        }

        exception<RequestValidationException> { call, cause ->
            call.respondText(status = HttpStatusCode.BadRequest, text = cause.message ?: "Bad Credentials")
        }

        exception<BadRequestException> { call, _ ->
            call.respondText(status = HttpStatusCode.BadRequest, text = "Bad Request")
        }

        exception<MissingCredentialException> { call, cause ->
            call.respondText(status = HttpStatusCode.BadRequest, text = cause.message)
        }

        exception<JWTVerificationException> { call, cause ->
            call.respondText(status = HttpStatusCode.Unauthorized, text = cause.message!!)
        }

        exception<UnauthorizedException> { call, cause ->
            cause.message?.let { call.respondText(status = HttpStatusCode.Unauthorized, text = it) } ?: run {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        exception<NoAccessException> { call, cause ->
            cause.message?.let { call.respondText(status = HttpStatusCode.Forbidden, text = it) } ?: run {
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        exception<Throwable> { call, cause ->
            call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error: $cause")
        }
    }
}