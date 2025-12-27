package com.github.radlance.autodispatch.domain.statistics

import kotlinx.serialization.Serializable

@Serializable
data class StatItem(
    val label: String,
    val count: Long
)
