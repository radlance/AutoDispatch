package com.github.radlance.autodispatch.auth.presentation


interface ValidateSignIn {

    fun validateLoginMessage(value: String): String

    fun validatePasswordMessage(value: String): String
}

internal class BaseValidateSignIn : ValidateSignIn {

    override fun validateLoginMessage(value: String): String = if (value.isBlank()) {
        "Логин обязателен"
    } else ""

    override fun validatePasswordMessage(value: String): String = when {
        value.isBlank() -> "Пароль обязателен"
        value.trim().length < 8 -> "Минимальная длина пароля – 8 симоволов"
        value.trim().length > 50 -> "Максимальная длина пароля – 50 символов"
        else -> ""
    }
}