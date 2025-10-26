package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class FiltersDto(
    val cities: List<CityDto>,
    val cargoTypes: List<CargoTypeDto>,
    val statuses: List<RequestStatusDto>,
    val drivers: List<UserFilterDto>,
    val vehicles: List<VehicleFilterDto>
)
