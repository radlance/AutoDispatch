package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.assignment.domain.VehicleStats

data class AssignmentFieldsState(
    val selectedDriverStats: DriverStats? = null,
    val selectedVehicleStats: VehicleStats? = null
)
