package com.github.radlance.autodispatch.driver.history.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.request.core.data.PointDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverHistoryDto(
    val id: Int,
    val status: StatusDto,
    val vehicle: VehicleDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val cargoTypeName: String,
    val assignedAt: String,
    val completedAt: String,
    val requestNumber: String
)