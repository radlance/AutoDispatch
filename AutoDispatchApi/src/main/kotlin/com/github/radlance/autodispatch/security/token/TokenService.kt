package com.github.radlance.autodispatch.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class TokenService(private val tokenConfig: TokenConfig) {

    private val audience = tokenConfig.audience
    private val issuer = tokenConfig.issuer
    private val secret = tokenConfig.secret

    fun generateAccessToken(userLogin: String): String {
        return JWT.create().apply {
            withAudience(audience)
            withIssuer(issuer)
            withClaim("login", userLogin)
            withExpiresAt(Date(System.currentTimeMillis() + tokenConfig.expiresIn))
        }.sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }

    fun verifyToken(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(secret)).apply {
            withAudience(audience)
            withIssuer(issuer)
        }.build()
    }
}