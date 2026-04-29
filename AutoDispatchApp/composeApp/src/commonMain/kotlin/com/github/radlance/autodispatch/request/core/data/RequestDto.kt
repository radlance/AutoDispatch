package com.github.radlance.autodispatch.request.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    val id: Int,
    val status: StatusDto,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val plannedLoadingAt: String,
    val plannedUnloadingAt: String,
    val actualLoadingAt: String?,
    val actualUnloadingAt: String?,
    val arrivedLoadingAt: String?,
    val arrivedUnloadingAt: String?,
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


