package com.github.radlance.autodispatch.security.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val secret: String,
    val expiresIn: Long,
    val totalExpiresIn: Long
)