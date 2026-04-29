package com.github.radlance.autodispatch.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val roleId: Int
)
