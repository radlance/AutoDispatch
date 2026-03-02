package com.github.radlance.autodispatch.domain.request

import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.delivery.DeliveryDocument
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val status: Status?,
    val origin: String?,
    val destination: String?,
    val transportationDescription: String?,
    val plannedLoadingAt: String?,
    val plannedUnloadingAt: String?,
    val actualLoadingAt: String?,
    val actualUnloadingAt: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val driverId: Int?,
    val driverFullName: String?,
    val customer: Customer,
    val vehicle: Vehicle?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?,
    val documents: List<DeliveryDocument> = emptyList()
)