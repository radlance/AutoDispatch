package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.UserEntity
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.util.loggedTransaction

class ProfileRepository {
    suspend fun userByLogin(login: String): User = loggedTransaction {
        UserEntity.find { UserTable.login eq login }.limit(1).first().toUser()
    }
}