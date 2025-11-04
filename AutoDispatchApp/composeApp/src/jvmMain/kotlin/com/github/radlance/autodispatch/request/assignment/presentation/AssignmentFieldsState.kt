package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

data class AssignmentFieldsState(
    val selectedDriverStats: DriverStats? = null,
)
