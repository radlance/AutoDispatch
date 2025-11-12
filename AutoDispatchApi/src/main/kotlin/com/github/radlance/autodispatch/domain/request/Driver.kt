package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val id: Int,
    val fullName: String
)
