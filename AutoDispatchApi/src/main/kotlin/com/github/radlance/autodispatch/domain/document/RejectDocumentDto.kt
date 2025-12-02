package com.github.radlance.autodispatch.domain.document

import kotlinx.serialization.Serializable

@Serializable
data class RejectDocumentDto(
    val reason: String
)
