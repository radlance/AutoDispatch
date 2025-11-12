package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class CargoType(
    val id: Int,
    val name: String
)