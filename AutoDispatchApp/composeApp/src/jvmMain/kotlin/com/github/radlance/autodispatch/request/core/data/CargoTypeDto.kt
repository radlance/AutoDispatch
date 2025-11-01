package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class CargoTypeDto(
    val id: Int,
    val name: String
)
