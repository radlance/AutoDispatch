package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class UserFilterDto(
    val id: Int,
    val fullName: String
)