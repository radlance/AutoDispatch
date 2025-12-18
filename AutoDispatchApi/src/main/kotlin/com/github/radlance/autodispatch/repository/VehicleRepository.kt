package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.domain.vehicle.VehicleDetailed
import com.github.radlance.autodispatch.exception.DeliveryStateException
import com.github.radlance.autodispatch.util.loggedTransaction
import io.ktor.server.plugins.*
import org.jetbrains.exposed.dao.id.EntityID
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

    suspend fun unassignedVehicles(): List<Vehicle> = loggedTransaction {
        VehicleTable
            .join(DriverTable, JoinType.LEFT, VehicleTable.id, DriverTable.vehicleId)
            .select(
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity
            )
            .where(DriverTable.vehicleId.isNull())
            .orderBy(VehicleTable.model, SortOrder.ASC)
            .map { row ->
                Vehicle(
                    id = row[VehicleTable.id].value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    payloadCapacity = row[VehicleTable.payloadCapacity]
                )
            }
    }

    suspend fun setDriverVehicle(driverId: Int, vehicleId: Int) = loggedTransaction {
        val isVehicleTaken = DriverTable
            .select(DriverTable.userId)
            .where {
                (DriverTable.vehicleId eq vehicleId) and (DriverTable.userId neq driverId)
            }
            .any()

        if (isVehicleTaken) {
            throw DeliveryStateException("Выбранный автомобиль уже занят другим водителем")
        }

        val updatedRows = DriverTable.update({ DriverTable.userId eq driverId }) {
            it[this.vehicleId] = EntityID(vehicleId, VehicleTable)
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
            .orderBy(VehicleTable.model, SortOrder.ASC)
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