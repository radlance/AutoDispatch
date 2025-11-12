package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.reuqest.core.data.CargoDto
import com.github.radlance.autodispatch.reuqest.core.data.CustomerDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleFilterDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDetailedDto(
    val id: Int,
    val status: RequestStatusDto,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val cargo: CargoDto,
    val loadingPoint: String,
    val unloadingPoint: String,
    val dispatcherFullName: String,
    val dispatcherPhoneNumber: String,
    val customer: CustomerDto,
    val vehicle: VehicleFilterDto,
    val createdAt: String,
    val updatedAt: String?,
    val requestNumber: String
)
