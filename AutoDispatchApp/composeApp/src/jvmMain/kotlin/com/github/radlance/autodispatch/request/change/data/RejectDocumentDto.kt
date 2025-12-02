package com.github.radlance.autodispatch.request.change.data

import kotlinx.serialization.Serializable

@Serializable
data class RejectDocumentDto(
    val reason: String
)