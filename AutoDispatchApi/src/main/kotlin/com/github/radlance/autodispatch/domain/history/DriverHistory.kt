package com.github.radlance.autodispatch.domain.history

import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.request.Point
import com.github.radlance.autodispatch.domain.request.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class DriverHistory(
    val id: Int,
    val status: Status,
    val vehicle: Vehicle,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargoTypeName: String,
    val assignedAt: String,
    val completedAt: String,
    val requestNumber: String
)