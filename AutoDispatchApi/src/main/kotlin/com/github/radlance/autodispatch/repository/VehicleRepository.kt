package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.domain.vehicle.VehicleDetailed
import com.github.radlance.autodispatch.exception.DriverBusyException
import com.github.radlance.autodispatch.util.loggedTransaction
import io.ktor.server.plugins.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.update

class VehicleRepository {

    suspend fun unassignedVehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResult<Vehicle> = loggedTransaction {

        val query = VehicleTable
            .join(DriverTable, JoinType.LEFT, VehicleTable.id, DriverTable.vehicleId)

        val conditions = mutableListOf<Op<Boolean>>()

        conditions += DriverTable.vehicleId.isNull()

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions += OrOp(
                listOf(
                    VehicleTable.model.lowerCase() like pattern,
                    VehicleTable.licensePlate.lowerCase() like pattern
                )
            )
        }

        val offset = (page - 1L) * pageSize

        val rawVehicles = query
            .select(
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity
            )
            .where(AndOp(conditions))
            .orderBy(VehicleTable.model, SortOrder.ASC)
            .limit(pageSize + 1)
            .offset(offset)
            .map { row ->
                Vehicle(
                    id = row[VehicleTable.id].value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    payloadCapacity = row[VehicleTable.payloadCapacity]
                )
            }

        val hasMore = rawVehicles.size > pageSize
        val vehicles = if (hasMore) rawVehicles.dropLast(1) else rawVehicles

        ListPaginatedResult(
            items = vehicles,
            hasMore = hasMore
        )
    }

    suspend fun assignDriverVehicle(driverId: Int, vehicleId: Int) = loggedTransaction {
        val driverWithVehicle = DriverTable
            .select(DriverTable.vehicleId)
            .where { DriverTable.userId eq driverId }
            .firstOrNull()

        if (driverWithVehicle?.get(DriverTable.vehicleId) != null) {
            throw DriverBusyException("У водителя уже назначен автомобиль")
        }

        val isVehicleTaken = DriverTable
            .select(DriverTable.userId)
            .where {
                (DriverTable.vehicleId eq vehicleId) and (DriverTable.userId neq driverId)
            }
            .any()
        if (isVehicleTaken) {
            throw DriverBusyException("Выбранный автомобиль уже занят другим водителем")
        }

        val hasNonCompletedDeliveries = AssignmentTable
            .join(RequestTable, JoinType.INNER, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.INNER, RequestTable.statusId, RequestStatusTable.id)
            .select(AssignmentTable.id)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestStatusTable.name notInList listOf("Завершена", "Отменена"))
            }
            .limit(1)
            .any()

        if (hasNonCompletedDeliveries) {
            throw DriverBusyException(
                "Нельзя назначить автомобиль водителю с незавершенными доставками (назначенные, в пути, на проверке или отклоненные)"
            )
        }

        val updatedRows = DriverTable.update({ DriverTable.userId eq driverId }) {
            it[this.vehicleId] = EntityID(vehicleId, VehicleTable)
        }
        if (updatedRows == 0) {
            throw NotFoundException("Водитель с ID $driverId не найден")
        }
    }


    suspend fun reassignDriverVehicle(driverId: Int, vehicleId: Int) = loggedTransaction {
        val isVehicleTaken = DriverTable
            .select(DriverTable.userId)
            .where {
                (DriverTable.vehicleId eq vehicleId) and (DriverTable.userId neq driverId)
            }
            .any()
        if (isVehicleTaken) {
            throw DriverBusyException("Выбранный автомобиль уже занят другим водителем")
        }

        val hasNonCompletedDeliveries = AssignmentTable
            .join(RequestTable, JoinType.INNER, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.INNER, RequestTable.statusId, RequestStatusTable.id)
            .select(AssignmentTable.driverId)
            .where {
                (AssignmentTable.driverId eq driverId) and (RequestStatusTable.name notInList listOf(
                    "Завершена",
                    "Отменена"
                ))
            }
            .any()
        if (hasNonCompletedDeliveries) {
            throw DriverBusyException("Нельзя назначить автомобиль водителю с незавершенными доставками (назначенные, в пути, на проверке или отклоненные)")
        }

        val updatedRows = DriverTable.update({ DriverTable.userId eq driverId }) {
            it[this.vehicleId] = EntityID(vehicleId, VehicleTable)
        }
        if (updatedRows == 0) {
            throw NotFoundException("Водитель с ID $driverId не найден")
        }
    }

    suspend fun unassignDriverVehicle(driverId: Int) = loggedTransaction {
        val currentDriver = DriverTable
            .select(DriverTable.vehicleId)
            .where { DriverTable.userId eq driverId }
            .firstOrNull()
        if (currentDriver == null) {
            throw NotFoundException("Водитель с ID $driverId не найден")
        }
        currentDriver.getOrNull(DriverTable.vehicleId)
            ?: throw DriverBusyException("У водителя нет назначенного автомобиля")
        val hasNonCompletedDeliveries = AssignmentTable
            .join(RequestTable, JoinType.INNER, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.INNER, RequestTable.statusId, RequestStatusTable.id)
            .select(AssignmentTable.id)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestStatusTable.name notInList listOf("Завершена", "Отменена"))
            }
            .limit(1)
            .any()
        if (hasNonCompletedDeliveries) {
            throw DriverBusyException(
                "Нельзя открепить автомобиль от водителя с незавершенными доставками (назначенные, в пути, на проверке или отклоненные)"
            )
        }
        val updatedRows = DriverTable.update({ DriverTable.userId eq driverId }) {
            it[this.vehicleId] = null
        }
        if (updatedRows == 0) {
            throw NotFoundException("Водитель с ID $driverId не найден")
        }
    }

    suspend fun vehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResult<VehicleDetailed> = loggedTransaction {

        val baseQuery = VehicleTable
            .join(DriverTable, JoinType.LEFT, VehicleTable.id, DriverTable.vehicleId)
            .join(UserTable, JoinType.LEFT, DriverTable.userId, UserTable.id)

        val condition: Op<Boolean> = if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            OrOp(
                listOf(
                    VehicleTable.model.lowerCase() like pattern,
                    VehicleTable.licensePlate.lowerCase() like pattern,
                    UserTable.fullName.lowerCase() like pattern
                )
            )
        } else {
            Op.TRUE
        }

        val total = baseQuery
            .select(VehicleTable.id.countDistinct())
            .where(condition)
            .single()[VehicleTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize

        val items = baseQuery
            .select(
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                UserTable.fullName
            )
            .where(condition)
            .orderBy(
                Coalesce(
                    DriverTable.updatedAt,
                    DriverTable.createdAt
                ), SortOrder.DESC_NULLS_LAST
            )
            .limit(pageSize)
            .offset(offset)
            .map { row ->
                VehicleDetailed(
                    id = row[VehicleTable.id].value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    payloadCapacity = row[VehicleTable.payloadCapacity],
                    driverFullName = row.getOrNull(UserTable.fullName)
                )
            }

        TablePaginatedResult(
            items = items,
            totalCount = total
        )
    }

}