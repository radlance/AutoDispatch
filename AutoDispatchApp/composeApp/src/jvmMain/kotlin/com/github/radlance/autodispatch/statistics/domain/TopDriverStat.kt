package com.github.radlance.autodispatch.statistics.domain

data class TopDriverStat(
    val fullName: String,
    val completedAssignments: Long,
    val currentStatus: String
)
