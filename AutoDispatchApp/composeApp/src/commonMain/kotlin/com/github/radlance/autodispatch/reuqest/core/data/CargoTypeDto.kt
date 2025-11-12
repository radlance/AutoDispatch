package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class CargoTypeDto(
    val id: Int,
    val name: String
)
