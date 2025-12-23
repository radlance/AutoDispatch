package com.github.radlance.autodispatch.domain.profile

import com.github.radlance.autodispatch.domain.request.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDetails(
    val fullName: String,
    val avatarUrl: String?,
    val deliveriesStats: DeliveriesStats,
    val phoneNumber: String,
    val vehicle: Vehicle?
)
