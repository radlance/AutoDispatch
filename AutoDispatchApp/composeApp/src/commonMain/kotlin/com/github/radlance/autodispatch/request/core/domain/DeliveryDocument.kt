package com.github.radlance.autodispatch.request.core.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDocument(
    val id: Int,
    val imageUrl: String,
    val uploadedAt: LocalDateTime,
    val type: DocumentType
)
