package com.github.radlance.autodispatch.delivery.core.domain

import com.github.radlance.autodispatch.reuqest.core.domain.Point
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import kotlinx.datetime.LocalDateTime

data class Delivery(
    val id: Int,
    val status: RequestStatus,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargoWeight: Double,
    val cargoVolume: Double?,
    val cargoTypeName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String,
)