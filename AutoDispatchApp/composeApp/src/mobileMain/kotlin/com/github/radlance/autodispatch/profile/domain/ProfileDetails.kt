package com.github.radlance.autodispatch.profile.domain

import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle

data class ProfileDetails(
    val fullName: String,
    val deliveriesStats: DeliveriesStats,
    val phoneNumber: String,
    val vehicle: Vehicle
)
