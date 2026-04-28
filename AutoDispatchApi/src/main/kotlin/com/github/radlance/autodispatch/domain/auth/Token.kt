package com.github.radlance.autodispatch.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)