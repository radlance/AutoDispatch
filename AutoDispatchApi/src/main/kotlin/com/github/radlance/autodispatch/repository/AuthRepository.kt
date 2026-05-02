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
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class AuthRepository {
    suspend fun create(user: RegisterUser, salt: String): User = loggedTransaction {
        val user = UserTable.insertReturning {
            it[login] = user.login
            it[email] = user.email
            it[passwordHash] = user.password
            it[this.salt] = salt
            it[fullName] = user.fullName
            it[phoneNumber] = user.phoneNumber
            it[roleId] = user.roleId
        }.first()

        User(
            id = user[UserTable.id].value,
            login = user[UserTable.login],
            fullName = user[UserTable.fullName],
            phoneNumber = user[UserTable.phoneNumber],
            isDispatcher = user[UserTable.roleId].value == 1,
            createdAt = user[UserTable.createdAt]?.toString()
        )
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

        UserTable.update({ UserTable.id eq userId }) {
            it[lastLoginAt] = CurrentTimestampWithTimeZone
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
}