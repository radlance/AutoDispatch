package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.UserEntity
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.auth.RegisterUser
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.domain.auth.UserWithPassword
import com.github.radlance.autodispatch.util.loggedTransaction

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
}