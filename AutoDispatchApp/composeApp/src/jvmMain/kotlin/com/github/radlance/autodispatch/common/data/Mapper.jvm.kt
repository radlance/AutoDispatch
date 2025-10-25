package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.data.RequestDto
import com.github.radlance.autodispatch.request.data.RequestResponseDto
import com.github.radlance.autodispatch.request.domain.Request
import com.github.radlance.autodispatch.request.domain.RequestResponse
import kotlinx.datetime.LocalDateTime

internal fun RequestResponseDto.toRequestResponse(): RequestResponse {
    return RequestResponse(
        cargoTypes = cargoTypes,
        requests = requests.map { it.toRequest() },
        departureCities = departureCities,
        destinationCities = destinationCities,
        statuses = statuses,
        drivers = drivers,
        vehicles = vehicles
    )
}

private fun RequestDto.toRequest(): Request {
    return Request(
        requestNumber = requestNumber,
        statusName = statusName,
        origin = origin,
        destination = destination,
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
        vehicleInfo = vehicleInfo,
        createdAt = createdAt?.let { LocalDateTime.parse(it.replace(" ", "T")) }
    )
}