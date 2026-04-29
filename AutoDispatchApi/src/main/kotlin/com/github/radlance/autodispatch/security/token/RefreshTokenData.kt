package com.github.radlance.autodispatch.security.token

import java.time.Instant

data class RefreshTokenData(
    val userId: Int,
    val expiresAt: Instant
)
