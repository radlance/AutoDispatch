package com.github.radlance.autodispatch.auth.presentation

import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.error_login_required
import autodispatch.composeapp.generated.resources.error_password_max_length
import autodispatch.composeapp.generated.resources.error_password_min_length
import autodispatch.composeapp.generated.resources.error_password_required
import org.jetbrains.compose.resources.StringResource


interface SignInValidator {

    fun validationLoginMessage(value: String): StringResource?

    fun validationPasswordMessage(value: String): StringResource?
}

internal class BaseSignInValidator : SignInValidator {

    override fun validationLoginMessage(value: String): StringResource? =
        if (value.isBlank()) {
            Res.string.error_login_required
        } else null

    override fun validationPasswordMessage(value: String): StringResource? =
        when {
            value.isBlank() -> Res.string.error_password_required
            value.trim().length < 8 -> Res.string.error_password_min_length
            value.trim().length > 50 -> Res.string.error_password_max_length
            else -> null
        }
}