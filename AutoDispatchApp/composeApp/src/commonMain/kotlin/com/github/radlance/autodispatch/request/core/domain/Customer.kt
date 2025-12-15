package com.github.radlance.autodispatch.request.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int,
    val organizationName: String,
    val email: String,
    val phoneNumber: String
)
