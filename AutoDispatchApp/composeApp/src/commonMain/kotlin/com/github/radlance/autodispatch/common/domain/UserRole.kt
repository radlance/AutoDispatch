package com.github.radlance.autodispatch.common.domain

enum class UserRole(
    val id: Int,
    val title: String
) {
    Dispatcher(1, "Диспетчер"),
    Driver(2, "Водитель"),
    Admin(3, "Администратор");

    companion object {
        fun fromId(id: Int): UserRole? =
            entries.firstOrNull { it.id == id }
    }
}

fun Int.toUserRole(): UserRole =
    UserRole.fromId(this) ?: error("Unknown user role id=$this")
