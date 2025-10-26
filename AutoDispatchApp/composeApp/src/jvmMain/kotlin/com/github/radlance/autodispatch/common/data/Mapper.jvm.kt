package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.data.CargoTypeDto
import com.github.radlance.autodispatch.request.data.CityDto
import com.github.radlance.autodispatch.request.data.FiltersDto
import com.github.radlance.autodispatch.request.data.PaginatedResultDto
import com.github.radlance.autodispatch.request.data.RequestDto
import com.github.radlance.autodispatch.request.data.RequestStatusDto
import com.github.radlance.autodispatch.request.data.UserFilterDto
import com.github.radlance.autodispatch.request.data.VehicleFilterDto
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City
import com.github.radlance.autodispatch.request.domain.Filters
import com.github.radlance.autodispatch.request.domain.PaginatedResult
import com.github.radlance.autodispatch.request.domain.Request
import com.github.radlance.autodispatch.request.domain.RequestStatus
import com.github.radlance.autodispatch.request.domain.UserFilter
import com.github.radlance.autodispatch.request.domain.VehicleFilter
import kotlinx.datetime.LocalDateTime

fun PaginatedResultDto<RequestDto>.toPaginatedResultRequest(): PaginatedResult<Request> {
    return PaginatedResult(
        items = items.map { it.toRequest() },
        totalCount = totalCount
    )
}

fun FiltersDto.toFilters(): Filters {
    return Filters(
        cities = cities.map { it.toCity() },
        cargoTypes = cargoTypes.map { it.toCargoType() },
        statuses = statuses.map { it.toRequestStatus() },
        drivers = drivers.map { it.toUserFilter() },
        vehicles = vehicles.map { it.toVehicleFilter() }
    )
}

private fun CityDto.toCity(): City {
    return City(
        id = id,
        name = name
    )
}

private fun CargoTypeDto.toCargoType(): CargoType {
    return CargoType(
        id = id,
        name = name
    )
}

private fun RequestStatusDto.toRequestStatus(): RequestStatus {
    return RequestStatus(
        id = id,
        name = name
    )
}

private fun UserFilterDto.toUserFilter(): UserFilter {
    return UserFilter(
        id = id,
        fullName = fullName
    )
}

private fun VehicleFilterDto.toVehicleFilter(): VehicleFilter {
    return VehicleFilter(
        id = id,
        model = model,
        licencePlate = licencePlate,
    )
}

private fun RequestDto.toRequest(): Request {
    return Request(
        id = id,
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
        startedTripAt = startedTripAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
        endedTripAt = endedTripAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
        driverFullName = driverFullName,
        organizationName = organizationName,
        organizationPhoneNumber = organizationPhoneNumber,
        organizationEmail = organizationEmail,
        vehicleInfo = vehicleInfo,
        createdAt = createdAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) }
    )
}