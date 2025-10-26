package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.domain.auth.UserWithPassword
import io.ktor.server.http.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<UserEntity>(UserTable)

    var login by UserTable.login
    var passwordHash by UserTable.passwordHash
    var salt by UserTable.salt
    var fullName by UserTable.fullName
    var phoneNumber by UserTable.phoneNumber
    var roleId by UserTable.roleId
    private val createdAt by UserTable.createdAt

    fun toUser(): User = User(
        id = id.value,
        login = login,
        fullName = fullName,
        phoneNumber = phoneNumber,
        isDispatcher = roleId == 1,
        createdAt = createdAt?.toString(),
    )

    fun toUserWithPassword(): UserWithPassword = UserWithPassword(
        id = id.value,
        login = login,
        passwordHash = passwordHash,
        salt = salt,
        fullName = fullName,
        phoneNumber = phoneNumber,
        roleId = roleId,
        createdAt = createdAt?.toHttpDateString()
    )
}