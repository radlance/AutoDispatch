package com.github.radlance.autodispatch.auth.presentation

data class SignInFieldsUiState(
    val loginFieldValue: String = "",
    val loginErrorMessage: String = "",
    val passwordFieldValue: String = "",
    val passwordErrorMessage: String = ""
)
