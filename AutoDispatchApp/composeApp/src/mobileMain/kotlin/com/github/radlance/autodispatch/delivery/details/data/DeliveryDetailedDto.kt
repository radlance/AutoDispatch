package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.request.core.data.CargoDto
import com.github.radlance.autodispatch.request.core.data.CustomerDto
import com.github.radlance.autodispatch.request.core.data.DeliveryDocumentDto
import com.github.radlance.autodispatch.request.core.data.PointDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDetailedDto(
    val id: Int,
    val status: StatusDto,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val plannedLoadingAt: String?,
    val plannedUnloadingAt: String?,
    val actualLoadingAt: String?,
    val actualUnloadingAt: String?,
    val cargo: CargoDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val dispatcherFullName: String,
    val dispatcherPhoneNumber: String,
    val customer: CustomerDto,
    val vehicle: VehicleDto,
    val createdAt: String,
    val updatedAt: String,
    val requestNumber: String,
    val rejectionReason: String?,
    val documents: List<DeliveryDocumentDto>
)
