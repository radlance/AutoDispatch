package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.domain.request.RequestResponse
import com.github.radlance.autodispatch.util.loggedTransaction

class RequestRepository {
    suspend fun requests(): RequestResponse = loggedTransaction {
        val departureCities = mutableSetOf<String>()
        val destinationCities = mutableSetOf<String>()
        val cargoTypes = mutableSetOf<String>()
        val statuses = mutableSetOf<String>()
        val drivers = mutableSetOf<String>()
        val vehicles = mutableSetOf<String>()

        val requests = mutableListOf<Request>()
        exec(
            """
                SELECT r.id,
                       r.request_number,
                       rs.name     AS status_name,
                       r.origin,
                       r.destination,
                       r.created_at,
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
                val departureCity = rs.getString("origin")
                val destinationCity = rs.getString("destination")
                val cargoTypeName = rs.getString("cargo_type_name")
                val status = rs.getString("status_name")
                val driver = rs.getString("driver_full_name")
                val vehicle = rs.getString("vehicle_info")

                cargoTypes.add(cargoTypeName)
                departureCities.add(departureCity)
                destinationCities.add(destinationCity)
                statuses.add(status)
                driver?.let { drivers.add(it) }
                vehicle?.let { vehicles.add(it) }

                requests.add(
                    Request(
                        id = rs.getInt("id"),
                        statusName = status,
                        origin = departureCity,
                        destination = destinationCity,
                        cargoTypeName = cargoTypeName,
                        cargoWeight = rs.getFloat("cargo_weight").toDouble(),
                        cargoVolume = rs.getFloat("cargo_volume").toDouble(),
                        cargoDescription = rs.getString("cargo_description"),
                        loadingPoint = rs.getString("loading_point"),
                        unloadingPoint = rs.getString("unloading_point"),
                        startedTripAt = rs.getTimestamp("started_trip_at")?.toString(),
                        endedTripAt = rs.getTimestamp("ended_trip_at")?.toString(),
                        driverFullName = driver,
                        organizationName = rs.getString("organization_name"),
                        organizationPhoneNumber = rs.getString("organization_phone_number"),
                        organizationEmail = rs.getString("organization_email"),
                        vehicleInfo = vehicle,
                        createdAt = rs.getTimestamp("created_at")?.toString(),
                        requestNumber = rs.getString("request_number")
                    )
                )
            }
        }
        return@loggedTransaction RequestResponse(
            cargoTypes = cargoTypes.toList(),
            requests = requests,
            departureCities = departureCities.toList(),
            destinationCities = destinationCities.toList(),
            statuses = statuses.toList(),
            drivers = drivers.toList(),
            vehicles = vehicles.toList()
        )
    }
}