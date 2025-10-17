package com.github.radlance.autodispatch.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginUser(
    val login: String,
    val password: String
)