package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFilterDto(
    val id: Int,
    val model: String,
    val licensePlate: String
)
