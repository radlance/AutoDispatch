package com.github.radlance.autodispatch.auth.domain

import com.github.radlance.autodispatch.common.domain.UserRole

data class LoginResponse(
    val accessToken: String,
    val role: UserRole
)
