package com.github.radlance.autodispatch.delivery.details.domain

import com.github.radlance.autodispatch.reuqest.core.domain.Cargo
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.reuqest.core.domain.Point
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter
import kotlinx.datetime.LocalDateTime

data class DeliveryDetailed(
    val id: Int,
    val status: RequestStatus,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val dispatcherFullName: String,
    val dispatcherPhoneNumber: String,
    val customer: Customer,
    val vehicle: VehicleFilter,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String
)
