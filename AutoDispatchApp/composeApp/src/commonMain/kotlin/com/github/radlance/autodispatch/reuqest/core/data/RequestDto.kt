package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    val id: Int,
    val status: RequestStatusDto,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val cargo: CargoDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val driverId: Int?,
    val driverFullName: String?,
    val customer: CustomerDto,
    val vehicle: VehicleDto?,
    val createdAt: String,
    val updatedAt: String?,
    val requestNumber: String,
    val documents: List<DeliveryDocumentDto> = emptyList()
)


