package com.github.radlance.autodispatch.delivery.details.domain

import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.request.core.domain.Cargo
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.DeliveryDocument
import com.github.radlance.autodispatch.request.core.domain.Point
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDetailed(
    val id: Int,
    val status: RequestStatus,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val plannedLoadingAt: String?,
    val plannedUnloadingAt: String?,
    val actualLoadingAt: String?,
    val actualUnloadingAt: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val dispatcherFullName: String,
    val dispatcherPhoneNumber: String,
    val customer: Customer,
    val vehicle: Vehicle,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val requestNumber: String,
    val rejectionReason: String?,
    val documents: List<DeliveryDocument>
)
