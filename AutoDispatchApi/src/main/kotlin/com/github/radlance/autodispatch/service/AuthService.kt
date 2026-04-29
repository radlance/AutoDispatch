package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.auth.AuthTokens
import com.github.radlance.autodispatch.domain.auth.LoginUser
import com.github.radlance.autodispatch.domain.auth.RefreshTokenRequest
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.domain.request.LoginResponse
import com.github.radlance.autodispatch.repository.AuthRepository
import com.github.radlance.autodispatch.security.hashing.HashingService
import com.github.radlance.autodispatch.security.hashing.SaltedHash
import com.github.radlance.autodispatch.security.token.TokenService
import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.security.token.TokenConfig

class AuthService(
    private val authRepository: AuthRepository,
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) {
    suspend fun register(user: RegisterUser): User {
        if (authRepository.getUserByLogin(user.login) != null) {
            throw MissingCredentialException("Login already taken! Input another login")
        }

        val saltedHash = hashingService.generateSaltedHash(user.password)

        val registerUser = RegisterUser(
            login = user.login,
            email = user.email,
            password = saltedHash.hash,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            roleId = user.roleId,
        )

        return authRepository.create(registerUser, saltedHash.salt)
    }

    suspend fun login(user: LoginUser): LoginResponse {
        val existingUser = authRepository.getUserByLogin(user.login)
            ?: throw MissingCredentialException("Incorrect login or password")

        val isValidPassword = hashingService.verify(
            value = user.password,
            saltedHash = SaltedHash(existingUser.passwordHash, existingUser.salt)
        )
        if (!isValidPassword) throw MissingCredentialException("Incorrect login or password")

        val accessToken = tokenService.generateAccessToken(userLogin = user.login)
        val refreshToken = tokenService.generateRefreshToken()

        authRepository.saveRefreshToken(
            token = refreshToken,
            userId = existingUser.id,
            expiresInMs = tokenConfig.refreshExpiresIn
        )

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            roleId = existingUser.roleId
        )
    }

    suspend fun refreshToken(request: RefreshTokenRequest): AuthTokens {
        val tokenData = authRepository.getRefreshTokenData(request.refreshToken)
            ?: throw MissingCredentialException("Invalid refresh token")

        if (tokenData.expiresAt.isBefore(java.time.Instant.now())) {
            authRepository.deleteRefreshToken(request.refreshToken)
            throw MissingCredentialException("Refresh token expired")
        }

        authRepository.deleteRefreshToken(request.refreshToken)

        val user = authRepository.getUserById(tokenData.userId)
            ?: throw MissingCredentialException("User not found")

        val newAccessToken = tokenService.generateAccessToken(user.login)
        val newRefreshToken = tokenService.generateRefreshToken()

        authRepository.saveRefreshToken(
            token = newRefreshToken,
            userId = tokenData.userId,
            expiresInMs = tokenConfig.refreshExpiresIn
        )

        return AuthTokens(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }
}