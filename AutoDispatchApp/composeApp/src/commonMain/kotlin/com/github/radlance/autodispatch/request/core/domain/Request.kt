package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.common.domain.RequestStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val status: RequestStatus,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val driverId: Int?,
    val driverFullName: String?,
    val customer: Customer,
    val vehicle: Vehicle?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String,
    val documents: List<DeliveryDocument> = emptyList()
)