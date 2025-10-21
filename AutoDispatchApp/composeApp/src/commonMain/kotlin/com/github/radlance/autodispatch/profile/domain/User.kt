package com.github.radlance.autodispatch.profile.domain

data class User(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val isActive: Boolean
)