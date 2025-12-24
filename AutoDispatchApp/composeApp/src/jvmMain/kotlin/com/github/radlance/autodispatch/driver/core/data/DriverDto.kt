package com.github.radlance.autodispatch.driver.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.profile.data.DeliveriesStatsDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverDto(
    val id: Int,
    val fullName: String,
    val avatarUrl: String?,
    val phoneNumber: String,
    val status: StatusDto,
    val vehicle: VehicleDto?,
    val deliveriesStats: DeliveriesStatsDto
)
