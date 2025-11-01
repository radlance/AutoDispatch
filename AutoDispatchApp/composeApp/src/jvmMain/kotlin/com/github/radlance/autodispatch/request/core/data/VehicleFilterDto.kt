package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFilterDto(
    val id: Int,
    val model: String,
    val licencePlate: String
)
