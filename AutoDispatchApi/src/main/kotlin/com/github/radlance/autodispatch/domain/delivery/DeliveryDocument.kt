package com.github.radlance.autodispatch.domain.delivery

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDocument(
    val id: Int,
    val imageUrl: String,
    val uploadedAt: String?
)