package com.github.radlance.autodispatch.delivery.domain

sealed interface DeliveryError {

    val message: String

    data class InternalError(override val message: String) : DeliveryError

    data class DriverBusyError(override val message: String) : DeliveryError

    data class DeliveryCanceledError(override val message: String) : DeliveryError

    data class GenericStateError(override val message: String) : DeliveryError

    data class BaseError(override val message: String) : DeliveryError
}