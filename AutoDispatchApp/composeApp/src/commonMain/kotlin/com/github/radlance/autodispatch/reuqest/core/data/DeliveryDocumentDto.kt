package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDocumentDto(
    val id: Int,
    val imageUrl: String,
    val uploadedAt: String
)