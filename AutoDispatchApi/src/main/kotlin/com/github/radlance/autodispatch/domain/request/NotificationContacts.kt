package com.github.radlance.autodispatch.domain.request

data class NotificationContacts(
    val reqNumber: String,
    val customerEmail: String?,
    val driverLogin: String,
    val driverEmail: String?,
    val driverFullName: String?,
    val driverPhoneNumber: String?
)