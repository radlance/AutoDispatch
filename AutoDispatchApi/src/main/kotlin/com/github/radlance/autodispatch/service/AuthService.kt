package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.auth.LoginUser
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.domain.auth.Token
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.repository.AuthRepository
import com.github.radlance.autodispatch.security.hashing.HashingService
import com.github.radlance.autodispatch.security.hashing.SaltedHash
import com.github.radlance.autodispatch.security.token.TokenService
import com.github.radlance.autodispatch.exception.MissingCredentialException

class AuthService(
    private val authRepository: AuthRepository,
    private val hashingService: HashingService,
    private val tokenService: TokenService
) {
    suspend fun register(user: RegisterUser): User {
        if (authRepository.getUserByLogin(user.login) != null) {
            throw MissingCredentialException("Login already taken! Input another login")
        }

        val saltedHash = hashingService.generateSaltedHash(user.password)

        val registerUser = RegisterUser(
            login = user.login,
            password = saltedHash.hash,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            roleId = user.roleId,
        )

        return authRepository.create(registerUser, saltedHash.salt)
    }

    suspend fun login(user: LoginUser): Token {
        val existingUser = authRepository.getUserByLogin(user.login)
            ?: throw MissingCredentialException("Incorrect login or password")

        val isValidPassword = hashingService.verify(
            value = user.password,
            saltedHash = SaltedHash(
                hash = existingUser.passwordHash,
                salt = existingUser.salt
            )
        )
        if (!isValidPassword) {
            throw MissingCredentialException("Incorrect login or password")
        }

        val accessToken = tokenService.generateToken(userLogin = user.login)
        return Token(accessToken = accessToken)
    }

    fun refreshToken(token: Token): Token = tokenService.refreshToken(token)
}