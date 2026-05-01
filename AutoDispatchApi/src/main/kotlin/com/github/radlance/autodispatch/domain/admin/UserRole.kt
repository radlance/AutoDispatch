package com.github.radlance.autodispatch.domain.admin

import kotlinx.serialization.Serializable

@Serializable
data class UserRole(
    val id: Int,
    val name: String
)
