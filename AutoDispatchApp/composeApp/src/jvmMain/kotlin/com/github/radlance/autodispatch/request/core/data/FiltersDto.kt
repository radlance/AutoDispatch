package com.github.radlance.autodispatch.request.core.data

import com.github.radlance.autodispatch.reuqest.core.data.CargoTypeDto
import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class FiltersDto(
    val cities: List<CityDto>,
    val cargoTypes: List<CargoTypeDto>,
    val statuses: List<StatusDto>,
    val drivers: List<UserFilterDto>,
    val vehicles: List<VehicleDto>
)
