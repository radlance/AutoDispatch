package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.domain.request.CargoType
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.domain.request.RequestResponse
import com.github.radlance.autodispatch.util.loggedTransaction

class RequestRepository {
    suspend fun requests(): RequestResponse = loggedTransaction {
        val cargoTypes = mutableSetOf<CargoType>()
        val requests = mutableListOf<Request>()

        exec(
            """
                SELECT r.id,
                       rs.name     AS status_name,
                       r.origin,
                       r.destination,
                       r.created_at,
                       ct.id     as cargo_type_id,
                       ct.name     as cargo_type_name,
                       r.cargo_weight,
                       r.cargo_volume,
                       r.cargo_description,
                       r.loading_point,
                       r.unloading_point,
                       r.started_trip_at,
                       r.ended_trip_at,
                       d.full_name as driver_full_name,
                       c.organization_name,
                       c.phone_number as organization_phone_number,
                       c.email as organization_email,
                       CASE
                           WHEN v.id IS NULL THEN NULL
                           ELSE CONCAT_WS(' ', v.model, '(' || v.license_plate || ')')
                           END     AS vehicle_info
                FROM request r
                         LEFT JOIN customer c ON r.customer_id = c.id
                         LEFT JOIN cargo_type ct ON r.cargo_type_id = ct.id
                         LEFT JOIN assignment a ON a.request_id = r.id
                         LEFT JOIN users d ON a.driver_id = d.id
                         LEFT JOIN vehicle v ON a.vehicle_id = v.id
                         LEFT JOIN request_status rs ON r.status_id = rs.id
                 ORDER BY r.created_at DESC;
            """.trimIndent()
        ) { rs ->
            while (rs.next()) {
                val cargoTypeName = rs.getString("cargo_type_name")
                cargoTypes.add(CargoType(id = rs.getInt("cargo_type_id"), name = cargoTypeName))
                requests.add(
                    Request(
                        id = rs.getInt("id"),
                        statusName = rs.getString("status_name"),
                        origin = rs.getString("origin"),
                        destination = rs.getString("destination"),
                        cargoTypeName = cargoTypeName,
                        cargoWeight = rs.getFloat("cargo_weight").toDouble(),
                        cargoVolume = rs.getFloat("cargo_volume").toDouble(),
                        cargoDescription = rs.getString("cargo_description"),
                        loadingPoint = rs.getString("loading_point"),
                        unloadingPoint = rs.getString("unloading_point"),
                        startedTripAt = rs.getTimestamp("started_trip_at")?.toString(),
                        endedTripAt = rs.getTimestamp("ended_trip_at")?.toString(),
                        driverFullName = rs.getString("driver_full_name"),
                        organizationName = rs.getString("organization_name"),
                        organizationPhoneNumber = rs.getString("organization_phone_number"),
                        organizationEmail = rs.getString("organization_email"),
                        vehicleInfo = rs.getString("vehicle_info"),
                        createdAt = rs.getTimestamp("created_at")?.toString()
                    )
                )
            }
        }
        return@loggedTransaction RequestResponse(
            cargoTypes = cargoTypes.toList(),
            requests = requests
        )
    }
}