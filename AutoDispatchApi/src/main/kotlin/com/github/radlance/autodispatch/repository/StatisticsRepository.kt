package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.*
import com.github.radlance.autodispatch.domain.statistics.DashboardStatistics
import com.github.radlance.autodispatch.domain.statistics.GeneralStats
import com.github.radlance.autodispatch.domain.statistics.PopularRouteStat
import com.github.radlance.autodispatch.domain.statistics.StatItem
import com.github.radlance.autodispatch.domain.statistics.TopDriverStat
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.*

class StatisticsRepository {

    suspend fun getStatistics(): DashboardStatistics = loggedTransaction {
        val totalRequests = RequestTable.selectAll().count()
        val completedRequests = RequestTable.select(RequestTable.id).where { RequestTable.statusId eq 4 }.count()
        val totalVehicles = VehicleTable.selectAll().count()
        val totalDrivers = DriverTable.selectAll().count()

        val requestsByStatus = RequestTable
            .join(RequestStatusTable, JoinType.INNER, RequestTable.statusId, RequestStatusTable.id)
            .select(RequestStatusTable.name, RequestTable.id.count())
            .groupBy(RequestStatusTable.name)
            .map { row ->
                StatItem(
                    label = row[RequestStatusTable.name],
                    count = row[RequestTable.id.count()]
                )
            }

        val requestsByCargo = RequestTable
            .join(CargoTypeTable, JoinType.INNER, RequestTable.cargoTypeId, CargoTypeTable.id)
            .select(CargoTypeTable.name, RequestTable.id.count())
            .groupBy(CargoTypeTable.name)
            .map { row ->
                StatItem(
                    label = row[CargoTypeTable.name],
                    count = row[RequestTable.id.count()]
                )
            }

        val driversByStatus = DriverTable
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .select(DriverStatusTable.name, DriverTable.userId.count())
            .groupBy(DriverStatusTable.name)
            .map { row ->
                StatItem(
                    label = row[DriverStatusTable.name],
                    count = row[DriverTable.userId.count()]
                )
            }

        val vehiclesWithDriverCount = DriverTable
            .select(DriverTable.vehicleId)
            .where { DriverTable.vehicleId.isNotNull() }
            .count()

        val vehiclesByStatus = listOf(
            StatItem("Занят", vehiclesWithDriverCount),
            StatItem("Свободен", totalVehicles - vehiclesWithDriverCount)
        )

        val topDrivers = AssignmentTable
            .join(UserTable, JoinType.INNER, AssignmentTable.driverId, UserTable.id)
            .join(DriverTable, JoinType.INNER, UserTable.id, DriverTable.userId)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .select(
                UserTable.fullName,
                UserTable.avatarUrl,
                DriverStatusTable.name,
                AssignmentTable.id.count()
            )
            .groupBy(UserTable.fullName, UserTable.avatarUrl, DriverStatusTable.name)
            .orderBy(AssignmentTable.id.count(), SortOrder.DESC)
            .limit(5)
            .map { row ->
                TopDriverStat(
                    fullName = row[UserTable.fullName],
                    avatarUrl = row[UserTable.avatarUrl],
                    completedAssignments = row[AssignmentTable.id.count()],
                    currentStatus = row[DriverStatusTable.name]
                )
            }

        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")

        val popularRoutes = RequestTable
            .join(originCity, JoinType.INNER, RequestTable.originId, originCity[CityTable.id])
            .join(destCity, JoinType.INNER, RequestTable.destinationId, destCity[CityTable.id])
            .select(
                originCity[CityTable.name],
                destCity[CityTable.name],
                RequestTable.id.count()
            )
            .groupBy(originCity[CityTable.name], destCity[CityTable.name])
            .orderBy(RequestTable.id.count(), SortOrder.DESC)
            .limit(5)
            .map { row ->
                PopularRouteStat(
                    originCity = row[originCity[CityTable.name]],
                    destinationCity = row[destCity[CityTable.name]],
                    requestCount = row[RequestTable.id.count()]
                )
            }

        DashboardStatistics(
            general = GeneralStats(
                totalRequests = totalRequests,
                completedRequests = completedRequests,
                totalVehicles = totalVehicles,
                totalDrivers = totalDrivers
            ),
            requestsByStatus = requestsByStatus,
            requestsByCargoType = requestsByCargo,
            driversByStatus = driversByStatus,
            vehiclesByStatus = vehiclesByStatus,
            topDrivers = topDrivers,
            popularRoutes = popularRoutes
        )
    }
}