package com.github.radlance.autodispatch.request.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class RequestFiltersDto(
    val cities: List<CityDto>,
    val cargoTypes: List<CargoTypeDto>,
    val statuses: List<StatusDto>,
    val drivers: List<UserFilterDto>,
    val vehicles: List<VehicleDto>
)
