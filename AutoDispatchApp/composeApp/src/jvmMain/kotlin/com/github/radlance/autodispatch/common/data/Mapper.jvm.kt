package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.data.CargoTypeDto
import com.github.radlance.autodispatch.request.data.RequestDto
import com.github.radlance.autodispatch.request.data.RequestResponseDto
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.Request
import com.github.radlance.autodispatch.request.domain.RequestResponse
import kotlinx.datetime.LocalDateTime

internal fun RequestResponseDto.toRequestResponse(): RequestResponse {
    return RequestResponse(
        cargoTypes = cargoTypes.map { it.toCargoType() },
        requests = requests.map { it.toRequest() }
    )
}

private fun CargoTypeDto.toCargoType(): CargoType {
    return CargoType(
        id = id,
        name = name
    )
}

private fun RequestDto.toRequest(): Request {
    return Request(
        id = id,
        statusName = statusName,
        origin = origin,
        destination = destination,
        tripDate = tripDate,
        cargoTypeName = cargoTypeName,
        cargoWeight = cargoWeight,
        cargoVolume = cargoVolume,
        cargoDescription = cargoDescription,
        loadingPoint = loadingPoint,
        unloadingPoint = unloadingPoint,
        startedTripAt = startedTripAt?.let { LocalDateTime.parse(it.replace(" ", "T")) },
        endedTripAt = endedTripAt?.let { LocalDateTime.parse(it.replace(" ", "T")) },
        driverFullName = driverFullName,
        organizationName = organizationName,
        organizationPhoneNumber = organizationPhoneNumber,
        organizationEmail = organizationEmail,
        vehicleInfo = vehicleInfo
    )
}