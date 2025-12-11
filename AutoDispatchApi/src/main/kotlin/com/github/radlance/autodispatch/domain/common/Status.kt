package com.github.radlance.autodispatch.domain.common

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Int,
    val name: String
)