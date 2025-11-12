package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.assignment.data.DriverStatsDto
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.core.data.CityDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.PaginatedResultDto
import com.github.radlance.autodispatch.request.core.data.UserFilterDto
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Filters
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult
import com.github.radlance.autodispatch.request.core.domain.UserFilter
import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import com.github.radlance.autodispatch.reuqest.core.domain.Request

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

fun ChangeRequest.toCreateRequestDto(): ChangeRequestDto {
    return ChangeRequestDto(
        loadingPoint = loadingPoint,
        unloadingPoint = unloadingPoint,
        cargoTypeId = cargoTypeId,
        cargoWeight = cargoWeight,
        cargoVolume = cargoVolume,
        cargoDescription = cargoDescription,
        customerName = customerName,
        customerEmail = customerEmail,
        customerPhone = customerPhone,
        originId = originId,
        destinationId = destinationId,
        transportationDescription = transportationDescription
    )
}

fun DriverStatsDto.toDriverStats(): DriverStats {
    return DriverStats(
        driverId = driverId,
        driverName = driverName,
        phoneNumber = phoneNumber,
        status = status,
        vehicleModel = vehicleModel,
        vehicleLicensePlate = vehicleLicensePlate,
        totalAssignedRequests = totalAssignedRequests
    )
}

private fun CityDto.toCity(): City {
    return City(
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