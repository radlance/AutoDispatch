package com.github.radlance.autodispatch.driver.request.data

import com.github.radlance.autodispatch.request.core.data.CargoDto
import com.github.radlance.autodispatch.request.core.data.CustomerDto
import com.github.radlance.autodispatch.request.core.data.PointDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverRequestDto(
    val id: Int,
    val requestNumber: String,
    val customer: CustomerDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val cargo: CargoDto,
    val createdAt: String,
    val updatedAt: String?
)
