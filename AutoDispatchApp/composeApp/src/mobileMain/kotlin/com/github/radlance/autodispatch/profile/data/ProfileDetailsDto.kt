package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.request.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDetailsDto(
    val fullName: String,
    val deliveriesStats: DeliveriesStatsDto,
    val phoneNumber: String,
    val vehicle: VehicleDto?
)
