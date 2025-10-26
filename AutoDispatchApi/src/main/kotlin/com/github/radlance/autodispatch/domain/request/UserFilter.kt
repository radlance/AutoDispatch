package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class UserFilter(
    val id: Int,
    val fullName: String
)