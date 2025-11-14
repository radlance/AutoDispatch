package com.github.radlance.autodispatch.delivery.domain

sealed interface DeliveryError {

    val message: String

    data class InternalError(override val message: String) : DeliveryError

    data class StateError(override val message: String) : DeliveryError

    data class BaseError(override val message: String) : DeliveryError
}