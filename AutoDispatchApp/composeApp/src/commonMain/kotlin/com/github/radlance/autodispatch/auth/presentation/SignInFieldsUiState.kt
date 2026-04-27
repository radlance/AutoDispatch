package com.github.radlance.autodispatch.auth.presentation

import org.jetbrains.compose.resources.StringResource

data class SignInFieldsUiState(
    val loginFieldValue: String = "",
    val loginErrorMessage: StringResource? = null,
    val passwordFieldValue: String = "",
    val passwordErrorMessage: StringResource? = null
)