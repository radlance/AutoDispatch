package com.github.radlance.autodispatch.domain.delivery

import com.github.radlance.autodispatch.domain.request.Cargo
import com.github.radlance.autodispatch.domain.request.Customer
import com.github.radlance.autodispatch.domain.request.RequestStatus
import com.github.radlance.autodispatch.domain.request.VehicleFilter
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDetailed(
    val id: Int,
    val status: RequestStatus?,
    val origin: String?,
    val destination: String?,
    val transportationDescription: String?,
    val cargo: Cargo,
    val loadingPoint: String?,
    val unloadingPoint: String?,
    val dispatcherFullName: String,
    val dispatcherPhoneNumber: String,
    val customer: Customer,
    val vehicle: VehicleFilter?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?,
)
