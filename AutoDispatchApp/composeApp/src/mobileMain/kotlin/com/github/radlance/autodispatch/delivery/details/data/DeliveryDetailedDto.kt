package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.reuqest.core.data.CargoDto
import com.github.radlance.autodispatch.reuqest.core.data.CustomerDto
import com.github.radlance.autodispatch.reuqest.core.data.PointDto
import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleDto
import com.github.radlance.autodispatch.reuqest.core.data.DeliveryDocumentDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDetailedDto(
    val id: Int,
    val status: StatusDto,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
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
