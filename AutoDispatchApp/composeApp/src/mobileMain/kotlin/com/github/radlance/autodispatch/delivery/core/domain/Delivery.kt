package com.github.radlance.autodispatch.delivery.core.domain

import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.request.core.domain.Point
import kotlinx.datetime.LocalDateTime

data class Delivery(
    val id: Int,
    val status: RequestStatus,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargoWeight: Double,
    val cargoTypeName: String,
    val plannedUnloadingAt: LocalDateTime?,
    val actualUnloadingAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String
)
