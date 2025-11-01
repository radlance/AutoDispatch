package com.github.radlance.autodispatch.auth.presentation


interface SignInValidator {

    fun validationLoginMessage(value: String): String

    fun validationPasswordMessage(value: String): String
}

internal class BaseSignInValidator : SignInValidator {

    override fun validationLoginMessage(value: String): String = if (value.isBlank()) {
        "Логин обязателен"
    } else ""

    override fun validationPasswordMessage(value: String): String = when {
        value.isBlank() -> "Пароль обязателен"
        value.trim().length < 8 -> "Минимальная длина пароля – 8 симоволов"
        value.trim().length > 50 -> "Максимальная длина пароля – 50 символов"
        else -> ""
    }
}