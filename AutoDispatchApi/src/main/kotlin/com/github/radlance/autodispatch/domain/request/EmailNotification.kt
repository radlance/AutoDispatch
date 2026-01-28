package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class EmailNotification(
    val email: String,
    val subject: String,
    val body: String
)