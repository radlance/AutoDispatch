package com.github.radlance.autodispatch.driver.history.domain

import com.github.radlance.autodispatch.common.domain.Status
import com.github.radlance.autodispatch.request.core.domain.Point
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import kotlinx.datetime.LocalDateTime

data class DriverHistory(
    val id: Int,
    val status: Status,
    val vehicle: Vehicle,
    val originCity: String,
    val destinationCity: String,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargoTypeName: String,
    val assignedAt: LocalDateTime,
    val completedAt: LocalDateTime,
    val requestNumber: String
)