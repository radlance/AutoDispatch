package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDetailsDto(
    val fullName: String,
    val deliveriesStats: DeliveriesStatsDto,
    val phoneNumber: String,
    val vehicleFilter: VehicleFilter
)
