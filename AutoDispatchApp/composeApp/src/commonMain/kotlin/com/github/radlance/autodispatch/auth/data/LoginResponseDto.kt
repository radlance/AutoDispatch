package com.github.radlance.autodispatch.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val roleId: Int
)
