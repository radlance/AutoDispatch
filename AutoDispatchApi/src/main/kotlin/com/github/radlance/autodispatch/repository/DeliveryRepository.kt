package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.CargoTypeTable // Добавлен импорт
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.delivery.Delivery // Ваш новый DTO
import com.github.radlance.autodispatch.domain.request.RequestStatus
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and

class DeliveryRepository {

    private fun mapDeliveryRow(row: ResultRow): Delivery = Delivery(
        id = row[RequestTable.id].value,
        requestNumber = row[RequestTable.requestNumber],
        status = RequestStatus(
            id = row[RequestStatusTable.id].value,
            name = row[RequestStatusTable.name]
        ),
        loadingPoint = row[RequestTable.loadingPoint],
        unloadingPoint = row[RequestTable.unloadingPoint],
        cargoWeight = row[RequestTable.cargoWeight],
        cargoVolume = row[RequestTable.cargoVolume],
        cargoTypeName = row[CargoTypeTable.name.alias("cargo_type_name")],
        createdAt = row[RequestTable.createdAt]?.toString(),
        updatedAt = row[RequestTable.updatedAt]?.toString()
    )

    suspend fun deliveries(driverLogin: String): List<Delivery> = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val joinQuery = RequestTable
            .join(AssignmentTable, JoinType.INNER, RequestTable.id, AssignmentTable.requestId)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id) // Добавлено

        val selectCols = listOf(
            RequestTable.id,
            RequestTable.requestNumber,
            RequestStatusTable.id,
            RequestStatusTable.name,
            RequestTable.loadingPoint,
            RequestTable.unloadingPoint,
            RequestTable.cargoWeight,
            RequestTable.cargoVolume,
            CargoTypeTable.name.alias("cargo_type_name"),
            RequestTable.createdAt,
            RequestTable.updatedAt
        )

        joinQuery
            .select(selectCols)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestTable.statusId inList listOf(2, 3))
            }
            .orderBy(Coalesce(RequestTable.updatedAt, RequestTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .map { mapDeliveryRow(it) }
    }

    suspend fun delivery(deliveryId: Int) = loggedTransaction {

    }
}