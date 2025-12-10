package com.github.radlance.autodispatch.driver.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverDto(
    val fullName: String,
    val phoneNumber: String,
    val status: StatusDto,
    val vehicle: VehicleDto,
    val deliveryCount: Int
)
