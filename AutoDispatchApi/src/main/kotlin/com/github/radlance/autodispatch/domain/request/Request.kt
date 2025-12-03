package com.github.radlance.autodispatch.domain.request

import com.github.radlance.autodispatch.domain.delivery.DeliveryDocument
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val status: RequestStatus?,
    val origin: String?,
    val destination: String?,
    val transportationDescription: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val driverId: Int?,
    val driverFullName: String?,
    val customer: Customer,
    val vehicleInfo: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?,
    val documents: List<DeliveryDocument> = emptyList()
)

@Serializable
data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)