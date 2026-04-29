package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.UserEntity
import com.github.radlance.autodispatch.database.table.RefreshTokenTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.domain.auth.UserWithPassword
import com.github.radlance.autodispatch.security.token.RefreshTokenData
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant

class AuthRepository {
    suspend fun create(user: RegisterUser, salt: String): User = loggedTransaction {
        UserEntity.new {
            login = user.login
            email = user.email
            passwordHash = user.password
            this.salt = salt
            fullName = user.fullName
            phoneNumber = user.phoneNumber
            roleId = user.roleId
        }.toUser()
    }

    suspend fun getUserByLogin(login: String): UserWithPassword? = loggedTransaction {
        UserEntity.find { UserTable.login eq login }.limit(1).firstOrNull()?.toUserWithPassword()
    }

    suspend fun getUserById(id: Int): UserWithPassword? = loggedTransaction {
        UserEntity.findById(id)?.toUserWithPassword()
    }

    suspend fun saveRefreshToken(token: String, userId: Int, expiresInMs: Long) = loggedTransaction {
        RefreshTokenTable.insert {
            it[this.token] = token
            it[this.userId] = userId
            it[this.expiresAt] = Instant.now().plusMillis(expiresInMs)
            it[this.createdAt] = Instant.now()
        }
    }

    suspend fun getRefreshTokenData(token: String): RefreshTokenData? = loggedTransaction {
        RefreshTokenTable
            .selectAll()
            .where { RefreshTokenTable.token eq token }
            .map {
                RefreshTokenData(
                    userId = it[RefreshTokenTable.userId].value,
                    expiresAt = it[RefreshTokenTable.expiresAt]
                )
            }
            .singleOrNull()
    }

    suspend fun deleteRefreshToken(token: String): Boolean = loggedTransaction {
        RefreshTokenTable.deleteWhere { RefreshTokenTable.token eq token } > 0
    }

    suspend fun deleteAllRefreshTokensByUserId(userId: Int) = loggedTransaction {
        RefreshTokenTable.deleteWhere { RefreshTokenTable.userId eq userId }
    }
}