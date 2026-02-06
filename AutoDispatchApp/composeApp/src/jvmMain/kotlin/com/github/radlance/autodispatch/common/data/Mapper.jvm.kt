package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult
import com.github.radlance.autodispatch.common.domain.toDriverStatus
import com.github.radlance.autodispatch.common.domain.toRequestStatus
import com.github.radlance.autodispatch.driver.core.data.DriverDto
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.history.data.DriverHistoryDto
import com.github.radlance.autodispatch.driver.history.domain.DriverHistory
import com.github.radlance.autodispatch.driver.request.data.DriverRequestDto
import com.github.radlance.autodispatch.driver.request.domain.DriverRequest
import com.github.radlance.autodispatch.request.assignment.data.DriverStatsDto
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.data.CoordsDto
import com.github.radlance.autodispatch.request.change.data.PointDetailedDto
import com.github.radlance.autodispatch.request.change.data.ReverseAddressDto
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.change.domain.Coords
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.core.data.CityDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.RequestDto
import com.github.radlance.autodispatch.request.core.data.TablePaginatedResultDto
import com.github.radlance.autodispatch.request.core.data.UserFilterDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Filters
import com.github.radlance.autodispatch.request.core.domain.Request
import com.github.radlance.autodispatch.request.core.domain.UserFilter
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import com.github.radlance.autodispatch.statistics.data.DashboardStatisticsDto
import com.github.radlance.autodispatch.statistics.data.GeneralStatsDto
import com.github.radlance.autodispatch.statistics.data.PopularRouteStatDto
import com.github.radlance.autodispatch.statistics.data.StatItemDto
import com.github.radlance.autodispatch.statistics.data.TopDriverStatDto
import com.github.radlance.autodispatch.statistics.domain.DashboardStatistics
import com.github.radlance.autodispatch.statistics.domain.GeneralStats
import com.github.radlance.autodispatch.statistics.domain.PopularRouteStat
import com.github.radlance.autodispatch.statistics.domain.StatItem
import com.github.radlance.autodispatch.statistics.domain.TopDriverStat
import com.github.radlance.autodispatch.vehicle.assignment.data.DriverWithoutVehicleDto
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverWithoutVehicle
import com.github.radlance.autodispatch.vehicle.core.data.VehicleDetailedDto
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed

fun TablePaginatedResultDto<RequestDto>.toPaginatedResultRequest(): TablePaginatedResult<Request> {
    return TablePaginatedResult(
        items = items.map { it.toRequest() },
        totalCount = totalCount
    )
}

fun FiltersDto.toFilters(): Filters {
    return Filters(
        cities = cities.map { it.toCity() },
        cargoTypes = cargoTypes.map { it.toCargoType() },
        statuses = statuses.map { it.id.toRequestStatus() },
        drivers = drivers.map { it.toUserFilter() },
        vehicles = vehicles.map { it.toVehicle() }
    )
}

fun ChangeRequest.toCreateRequestDto(): ChangeRequestDto {
    return ChangeRequestDto(
        loadingAddress = loadingAddress,
        loadingLat = loadingLat,
        loadingLon = loadingLon,
        unloadingAddress = unloadingAddress,
        unloadingLat = unloadingLat,
        unloadingLon = unloadingLon,
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

private fun DriverStatsDto.toDriverStats(): DriverStats {
    return DriverStats(
        driverId = driverId,
        driverName = driverName,
        phoneNumber = phoneNumber,
        driverStatus = driverStatus.id.toDriverStatus(),
        vehicleModel = vehicleModel,
        vehicleLicensePlate = vehicleLicensePlate,
        vehiclePayloadCapacity = vehiclePayloadCapacity,
        totalAssignedRequests = totalAssignedRequests
    )
}

fun ListPaginatedResultDto<DriverStatsDto>.toDriverStatsListPaginatedResult(): ListPaginatedResult<DriverStats> {
    return ListPaginatedResult(
        items = items.map { it.toDriverStats() },
        hasMore = hasMore
    )
}

fun CoordsDto.toCoords(): Coords {
    return Coords(
        lat = lat,
        lon = lon
    )
}

fun PointDetailedDto.toPointDetailed(): PointDetailed {
    return PointDetailed(
        placeId = placeId,
        lat = lat.toDouble(),
        lon = lon.toDouble(),
        importance = importance,
        name = name,
        displayName = displayName,
        boundingBox = boundingBox,
        geoJson = geoJson
    )
}

fun TablePaginatedResultDto<DriverDto>.toPaginatedResultDriver(): TablePaginatedResult<Driver> {
    return TablePaginatedResult(
        items = items.map { it.toDriver() },
        totalCount = totalCount
    )
}

fun ListPaginatedResultDto<DriverHistoryDto>.toDriverHistoryListPaginatedResult(): ListPaginatedResult<DriverHistory> {
    return ListPaginatedResult(
        items = items.map { it.toDriverHistory() },
        hasMore = hasMore
    )
}

fun DriverHistoryDto.toDriverHistory(): DriverHistory {
    return DriverHistory(
        id = id,
        status = status.id.toRequestStatus(),
        vehicle = vehicle.toVehicle(),
        originCity = originCity,
        destinationCity = destinationCity,
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        cargoTypeName = cargoTypeName,
        assignedAt = assignedAt.toLocalDateTimeFromUtc(),
        completedAt = completedAt.toLocalDateTimeFromUtc(),
        requestNumber = requestNumber
    )
}

fun ListPaginatedResultDto<DriverRequestDto>.toDriverRequestListPaginatedResult(): ListPaginatedResult<DriverRequest> {
    return ListPaginatedResult(
        items = items.map { it.toDriverRequest() },
        hasMore = hasMore
    )
}

fun TablePaginatedResultDto<VehicleDetailedDto>.toPaginatedResultVehicleDetails(): TablePaginatedResult<VehicleDetailed> {
    return TablePaginatedResult(
        items = items.map { it.toVehicleDetailed() },
        totalCount = totalCount
    )
}

fun ListPaginatedResultDto<VehicleDto>.toVehicleListPaginatedResult(): ListPaginatedResult<Vehicle> {
    return ListPaginatedResult(
        items = items.map { it.toVehicle() },
        hasMore = hasMore
    )
}

fun ListPaginatedResultDto<DriverWithoutVehicleDto>.toDriverWithoutVehicleListPaginatedResult(): ListPaginatedResult<DriverWithoutVehicle> {
    return ListPaginatedResult(
        items = items.map { it.toDriverWithoutCar() },
        hasMore = hasMore
    )
}

fun DashboardStatisticsDto.toDashboardStatistics(): DashboardStatistics {
    return DashboardStatistics(
        general = general.toGeneralStats(),
        requestsByStatus = requestsByStatus.map { it.toStatItem() },
        requestsByCargoType = requestsByCargoType.map { it.toStatItem() },
        driversByStatus = driversByStatus.map { it.toStatItem() },
        vehiclesByStatus = vehiclesByStatus.map { it.toStatItem() },
        topDrivers = topDrivers.map { it.toTopDriverStat() },
        popularRoutes = popularRoutes.map { it.toPopularRouteStat() }
    )
}

fun ReverseAddressDto.belongsTo(selectedCity: String): Boolean {
    return when {
        state?.equals(selectedCity, ignoreCase = true) == true -> true
        city?.equals(selectedCity, ignoreCase = true) == true -> true
        town?.equals(selectedCity, ignoreCase = true) == true -> true
        else -> false
    }
}

private fun StatItemDto.toStatItem(): StatItem {
    return StatItem(
        label = label,
        count = count
    )
}

private fun GeneralStatsDto.toGeneralStats(): GeneralStats {
    return GeneralStats(
        totalRequests = totalRequests,
        completedRequests = completedRequests,
        totalVehicles = totalVehicles,
        totalDrivers = totalDrivers
    )
}

private fun PopularRouteStatDto.toPopularRouteStat(): PopularRouteStat {
    return PopularRouteStat(
        originCity = originCity,
        destinationCity = destinationCity,
        requestCount = requestCount
    )
}

private fun TopDriverStatDto.toTopDriverStat(): TopDriverStat {
    return TopDriverStat(
        fullName = fullName,
        avatarUrl = avatarUrl?.asImageUrl(),
        completedAssignments = completedAssignments,
        currentStatus = currentStatus.id.toDriverStatus()
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

private fun DriverDto.toDriver(): Driver {
    return Driver(
        id = id,
        fullName = fullName,
        avatarUrl = avatarUrl?.asImageUrl(),
        phoneNumber = phoneNumber,
        status = status.id.toDriverStatus(),
        vehicle = vehicle?.toVehicle(),
        deliveriesStats = deliveriesStats.toDeliveriesStats()
    )
}

private fun DriverRequestDto.toDriverRequest(): DriverRequest {
    return DriverRequest(
        id = id,
        requestNumber = requestNumber,
        customer = customer.toCustomer(),
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        cargo = cargo.toCargo(),
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc()
    )
}

private fun VehicleDetailedDto.toVehicleDetailed(): VehicleDetailed {
    return VehicleDetailed(
        id = id,
        model = model,
        licensePlate = licensePlate,
        payloadCapacity = payloadCapacity,
        driverFullName = driverFullName
    )
}

private fun DriverWithoutVehicleDto.toDriverWithoutCar(): DriverWithoutVehicle {
    return DriverWithoutVehicle(
        id = id,
        fullName = fullName,
        phoneNumber = phoneNumber,
        totalDeliveries = totalDeliveries
    )
}