package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

data class DriverAssignmentFieldsState(
    val selectedDriverStats: DriverStats? = null,
)
