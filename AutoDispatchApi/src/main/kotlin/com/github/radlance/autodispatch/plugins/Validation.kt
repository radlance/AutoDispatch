package com.github.radlance.autodispatch.plugins

import com.auth0.jwt.exceptions.JWTVerificationException
import com.github.radlance.autodispatch.domain.auth.LoginUser
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.exception.DeliveryCanceledException
import com.github.radlance.autodispatch.exception.DeliveryForbiddenException
import com.github.radlance.autodispatch.exception.DeliveryNotFoundException
import com.github.radlance.autodispatch.exception.DriverBusyException
import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.exception.NoAccessException
import com.github.radlance.autodispatch.exception.StateConflictException
import com.github.radlance.autodispatch.exception.UnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
private data class ErrorResponse(
    val message: String,
    val errorCode: String
)

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<RegisterUser> { user ->
            when {
                user.login.trim().isBlank() -> {
                    ValidationResult.Invalid("Invalid login format")
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
        exception<DriverBusyException> { call, cause ->
            val error = ErrorResponse(
                message = cause.message,
                errorCode = "DRIVER_BUSY"
            )
            call.respond(HttpStatusCode.Conflict, error)
        }

        exception<DeliveryCanceledException> { call, cause ->
            val error = ErrorResponse(
                message = cause.message,
                errorCode = "DELIVERY_CANCELED"
            )
            call.respond(HttpStatusCode.Conflict, error)
        }

        exception<StateConflictException> { call, cause ->
            val error = ErrorResponse(
                message = cause.message,
                errorCode = "STATE_CONFLICT"
            )
            call.respond(HttpStatusCode.Conflict, error)
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

        exception<NoSuchElementException> { call, cause ->
            call.respondText(
                status = HttpStatusCode.NotFound,
                text = cause.message ?: "Not found"
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error: $cause")
        }
    }
}
