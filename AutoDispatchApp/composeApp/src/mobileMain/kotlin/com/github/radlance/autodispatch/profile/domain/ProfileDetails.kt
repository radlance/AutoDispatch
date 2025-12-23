package com.github.radlance.autodispatch.profile.domain

import com.github.radlance.autodispatch.request.core.domain.Vehicle

data class ProfileDetails(
    val fullName: String,
    val avatarUrl: String?,
    val deliveriesStats: DeliveriesStats,
    val phoneNumber: String,
    val vehicle: Vehicle?
)
