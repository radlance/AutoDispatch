package com.github.radlance.autodispatch.admin.domain

enum class UserStatus(
    val id: Int,
    val title: String
) {
    Active(1, "Активен"),
    Blocked(2, "Заблокирован"),
    Deleted(3, "Удалён");

    companion object {
        fun fromId(id: Int?): UserStatus? =
            entries.firstOrNull { it.id == id }
    }
}

fun Int.toUserStatus(): UserStatus =
    UserStatus.fromId(this)
        ?: error("Unknown user status id=$this")