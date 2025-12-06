package com.github.radlance.autodispatch.domain.profile

import com.github.radlance.autodispatch.domain.request.VehicleFilter
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDetails(
    val fullName: String,
    val deliveriesStats: DeliveriesStats,
    val phoneNumber: String,
    val vehicleFilter: VehicleFilter
)
