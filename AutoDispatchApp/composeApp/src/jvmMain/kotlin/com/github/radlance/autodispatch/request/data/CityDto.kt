package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: Int,
    val name: String
)
