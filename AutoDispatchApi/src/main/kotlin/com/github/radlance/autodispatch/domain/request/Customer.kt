package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int,
    val organizationName: String,
    val email: String,
    val phoneNumber: String?
)
