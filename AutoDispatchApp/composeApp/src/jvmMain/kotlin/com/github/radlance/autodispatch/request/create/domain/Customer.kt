package com.github.radlance.autodispatch.request.create.domain

data class Customer(
    val id: Int,
    val organizationName: String,
    val email: String,
    val phoneNumber: String?
)
