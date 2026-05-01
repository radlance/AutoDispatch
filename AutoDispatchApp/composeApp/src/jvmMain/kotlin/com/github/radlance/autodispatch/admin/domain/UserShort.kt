package com.github.radlance.autodispatch.admin.domain

data class UserShort(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?
)