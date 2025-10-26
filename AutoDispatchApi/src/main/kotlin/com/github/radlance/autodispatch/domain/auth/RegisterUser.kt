package com.github.radlance.autodispatch.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val login: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val roleId: Int
)