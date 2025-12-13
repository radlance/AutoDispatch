package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.DriverStatusTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.driver.Driver
import com.github.radlance.autodispatch.domain.driver.DriverStats
import com.github.radlance.autodispatch.domain.request.PaginatedResult
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.sum

class DriverRepository {

    suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): PaginatedResult<Driver> = loggedTransaction {
        val query = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id) // <--- LEFT JOIN
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)

        val condition: Op<Boolean> = if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            OrOp(
                listOf(
                    UserTable.fullName.lowerCase() like pattern,
                    UserTable.phoneNumber.lowerCase() like pattern,
                    VehicleTable.model.lowerCase() like pattern,
                    VehicleTable.licensePlate.lowerCase() like pattern
                )
            )
        } else {
            Op.TRUE
        }

        val total = query
            .select(UserTable.id.countDistinct())
            .where(condition)
            .single()[UserTable.id.countDistinct()]

        val deliveryCountParam = AssignmentTable.id.count()
        val offset = (page - 1L) * pageSize

        val items = query
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                deliveryCountParam
            )
            .where(condition)
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity
            )
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .limit(pageSize)
            .offset(offset)
            .map { row ->
                val vehicle = row.getOrNull(VehicleTable.id)?.let {
                    Vehicle(
                        id = it.value,
                        model = row[VehicleTable.model],
                        licensePlate = row[VehicleTable.licensePlate],
                        payloadCapacity = row[VehicleTable.payloadCapacity]
                    )
                }

                Driver(
                    id = row[UserTable.id].value,
                    fullName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    status = Status(
                        id = row[DriverStatusTable.id].value,
                        name = row[DriverStatusTable.name]
                    ),
                    vehicle = vehicle,
                    deliveryCount = row[deliveryCountParam].toInt()
                )
            }

        PaginatedResult(items = items, totalCount = total)
    }

    suspend fun driverStats(): List<DriverStats> = loggedTransaction {
        val requestCount = Case()
            .When(RequestTable.statusId inList listOf(1, 2, 3), longLiteral(1))
            .Else(longLiteral(0))
            .sum()

        val statusOrder = Case()
            .When(DriverStatusTable.id eq 1, intLiteral(1))
            .When(DriverStatusTable.id eq 2, intLiteral(2))
            .When(DriverStatusTable.id eq 3, intLiteral(3))
            .Else(intLiteral(4))

        DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                requestCount
            )
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity
            )
            .orderBy(statusOrder, SortOrder.ASC)
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .map { row ->
                DriverStats(
                    driverId = row[UserTable.id].value,
                    driverName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    driverStatus = row[DriverStatusTable.name],
                    vehicleModel = row[VehicleTable.model],
                    vehicleLicensePlate = row[VehicleTable.licensePlate],
                    vehiclePayloadCapacity = row[VehicleTable.payloadCapacity],
                    totalAssignedRequests = row[requestCount] ?: 0L
                )
            }
    }
}