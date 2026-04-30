package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDocumentDto(
    val id: Int,
    val imageUrl: String,
    val uploadedAt: String,
    val typeId: Int
)