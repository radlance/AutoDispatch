package com.github.radlance.autodispatch.auth.domain

data class LoginResponse(
    val accessToken: String,
    val roleId: Int
)
