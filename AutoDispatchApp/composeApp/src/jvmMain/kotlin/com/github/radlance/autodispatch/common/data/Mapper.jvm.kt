package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.data.CustomerDto
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.change.domain.Customer
import com.github.radlance.autodispatch.request.assignment.data.VehicleStatsDto
import com.github.radlance.autodispatch.request.core.data.CargoTypeDto
import com.github.radlance.autodispatch.request.core.data.CityDto
import com.github.radlance.autodispatch.request.assignment.data.DriverStatsDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.PaginatedResultDto
import com.github.radlance.autodispatch.request.assignment.data.RequestAssignmentDto
import com.github.radlance.autodispatch.request.core.data.RequestDto
import com.github.radlance.autodispatch.request.core.data.RequestStatusDto
import com.github.radlance.autodispatch.request.core.data.UserFilterDto
import com.github.radlance.autodispatch.request.core.data.VehicleFilterDto
import com.github.radlance.autodispatch.request.assignment.domain.VehicleStats
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.core.domain.Filters
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult
import com.github.radlance.autodispatch.request.core.domain.Request
import com.github.radlance.autodispatch.request.assignment.domain.RequestAssignment
import com.github.radlance.autodispatch.request.core.domain.RequestStatus
import com.github.radlance.autodispatch.request.core.domain.UserFilter
import com.github.radlance.autodispatch.request.core.domain.VehicleFilter
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

fun CustomerDto.toCustomer(): Customer {
    return Customer(
        id = id,
        organizationName = organizationName,
        email = email,
        phoneNumber = phoneNumber
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

fun RequestAssignmentDto.toRequestAssignment(): RequestAssignment {
    return RequestAssignment(
        driversStats = driversStats.map { it.toDriverStats() },
        vehiclesStats = vehiclesStats.map { it.toAvailableVehicle() }
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
        status = status.toRequestStatus(),
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
        transportationDescription = transportationDescription,
        createdAt = createdAt.removeSuffix("Z").let { LocalDateTime.parse(it) }
    )
}

private fun DriverStatsDto.toDriverStats(): DriverStats {
    return DriverStats(
        driverName = driverName,
        phoneNumber = phoneNumber,
        status = status,
        totalAssignedRequests = totalAssignedRequests
    )
}

private fun VehicleStatsDto.toAvailableVehicle(): VehicleStats {
    return VehicleStats(
        id = id,
        model = model,
        licencePlate = licencePlate,
        vehicleStatus = vehicleStatus
    )
}