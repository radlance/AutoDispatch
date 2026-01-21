package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.DriverStatusTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.driver.Driver
import com.github.radlance.autodispatch.domain.driver.DriverStats
import com.github.radlance.autodispatch.domain.driver.DriverWithoutCar
import com.github.radlance.autodispatch.domain.profile.DeliveriesStats
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
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
    ): TablePaginatedResult<Driver> = loggedTransaction {

        val baseQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)

        val condition: Op<Boolean> =
            if (!searchQuery.isNullOrBlank()) {
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

        val total = baseQuery
            .select(UserTable.id.countDistinct())
            .where(condition)
            .single()[UserTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize
        val countExpression = AssignmentTable.id.count()

        val rows = baseQuery
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.avatarUrl,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                RequestStatusTable.name,
                countExpression
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
                VehicleTable.payloadCapacity,
                RequestStatusTable.name,
                DriverTable.updatedAt,
                DriverTable.createdAt
            )
            .orderBy(
                Coalesce(
                    DriverTable.updatedAt,
                    DriverTable.createdAt
                ), SortOrder.DESC_NULLS_LAST
            )
            .limit(pageSize)
            .offset(offset)
            .toList()

        val items = rows
            .groupBy { it[UserTable.id].value }
            .map { (_, driverRows) ->

                val first = driverRows.first()

                val statsMap = driverRows
                    .filter { it.getOrNull(RequestStatusTable.name) != null }
                    .associate {
                        it[RequestStatusTable.name] to it[countExpression].toInt()
                    }

                val deliveriesStats = DeliveriesStats(
                    totalCount = statsMap.values.sum(),
                    activeCount = (statsMap["Назначена"] ?: 0) + (statsMap["В пути"] ?: 0),
                    completedCount = statsMap["Завершена"] ?: 0,
                    canceledCount = statsMap["Отменена"] ?: 0,
                    onCheckCount = statsMap["На проверке"] ?: 0,
                    rejectedCount = statsMap["Отклонена"] ?: 0
                )

                val vehicle = first.getOrNull(VehicleTable.id)?.value?.let { id ->
                    Vehicle(
                        id = id,
                        model = first[VehicleTable.model],
                        licensePlate = first[VehicleTable.licensePlate],
                        payloadCapacity = first[VehicleTable.payloadCapacity]
                    )
                }

                Driver(
                    id = first[UserTable.id].value,
                    fullName = first[UserTable.fullName],
                    avatarUrl = first[UserTable.avatarUrl],
                    phoneNumber = first[UserTable.phoneNumber],
                    status = Status(
                        id = first[DriverStatusTable.id].value,
                        name = first[DriverStatusTable.name]
                    ),
                    vehicle = vehicle,
                    deliveriesStats = deliveriesStats
                )
            }

        TablePaginatedResult(
            items = items,
            totalCount = total
        )
    }


    suspend fun driverStats(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResult<DriverStats> = loggedTransaction {

        val requestCount = Case()
            .When(RequestTable.statusId inList listOf(1, 2, 3), longLiteral(1))
            .Else(longLiteral(0))
            .sum()

        val statusOrder = Case()
            .When(DriverStatusTable.id eq 1, intLiteral(1))
            .When(DriverStatusTable.id eq 2, intLiteral(2))
            .When(DriverStatusTable.id eq 3, intLiteral(3))
            .Else(intLiteral(4))

        val joinQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)

        val conditions = mutableListOf<Op<Boolean>>()

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions += OrOp(
                listOf(
                    UserTable.fullName.lowerCase() like pattern,
                    UserTable.phoneNumber.lowerCase() like pattern,
                    DriverStatusTable.name.lowerCase() like pattern,
                    VehicleTable.model.lowerCase() like pattern,
                    VehicleTable.licensePlate.lowerCase() like pattern
                )
            )
        }

        val offset = (page - 1L) * pageSize

        val rawStats = joinQuery
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
            .where(
                if (conditions.isEmpty()) Op.TRUE else AndOp(conditions)
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
            .limit(pageSize + 1)
            .offset(offset)
            .map { row ->
                DriverStats(
                    driverId = row[UserTable.id].value,
                    driverName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    driverStatus = Status(
                        id = row[DriverStatusTable.id].value,
                        name = row[DriverStatusTable.name]
                    ),
                    vehicleModel = row[VehicleTable.model],
                    vehicleLicensePlate = row[VehicleTable.licensePlate],
                    vehiclePayloadCapacity = row[VehicleTable.payloadCapacity],
                    totalAssignedRequests = row[requestCount] ?: 0L
                )
            }

        val hasMore = rawStats.size > pageSize
        val stats = if (hasMore) rawStats.dropLast(1) else rawStats

        ListPaginatedResult(
            items = stats,
            hasMore = hasMore
        )
    }

    suspend fun driversWithoutCar(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResult<DriverWithoutCar> = loggedTransaction {

        val baseQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)

        val conditions = mutableListOf<Op<Boolean>>()

        conditions.add(DriverTable.vehicleId.isNull())

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions.add(
                OrOp(
                    listOf(
                        UserTable.fullName.lowerCase() like pattern,
                        UserTable.phoneNumber.lowerCase() like pattern
                    )
                )
            )
        }

        val finalCondition = if (conditions.isEmpty()) Op.TRUE else AndOp(conditions)
        val deliveriesCount = AssignmentTable.id.count()
        val offset = (page - 1L) * pageSize

        val rows = baseQuery
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                deliveriesCount
            )
            .where(finalCondition)
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber
            )
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .limit(pageSize + 1)
            .offset(offset)
            .map { row ->
                DriverWithoutCar(
                    id = row[UserTable.id].value,
                    fullName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    totalDeliveries = row[deliveriesCount].toInt()
                )
            }

        val hasMore = rows.size > pageSize
        val items = if (hasMore) rows.dropLast(1) else rows

        ListPaginatedResult(
            items = items,
            hasMore = hasMore
        )
    }
}