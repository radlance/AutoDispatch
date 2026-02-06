package com.github.radlance.autodispatch.request.change.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseDto(
    val address: ReverseAddressDto?
)

@Serializable
data class ReverseAddressDto(
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val municipality: String? = null,
    val state: String? = null,
    @SerialName("ISO3166-2-lvl4")
    val isoRegion: String? = null
)
