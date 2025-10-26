package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFilterDto(
    val id: Int,
    val model: String,
    val licencePlate: String
)
