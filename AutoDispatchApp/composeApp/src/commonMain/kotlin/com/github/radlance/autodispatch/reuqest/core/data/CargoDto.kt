package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class CargoDto(
    val type: CargoTypeDto,
    val weight: Double,
    val volume: Double?,
    val description: String?
)
