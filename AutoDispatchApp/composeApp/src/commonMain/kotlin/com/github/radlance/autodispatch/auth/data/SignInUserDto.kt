package com.github.radlance.autodispatch.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class SignInUserDto(
    val login: String,
    val password: String
)