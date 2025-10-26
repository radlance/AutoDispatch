package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class CargoType(
    val id: Int,
    val name: String
)