package com.github.radlance.autodispatch.delivery.domain

sealed interface RequestError {

    val message: String

    data class InternalError(override val message: String) : RequestError

    data class DriverBusyError(override val message: String) : RequestError

    data class DeliveryCanceledError(override val message: String) : RequestError

    data class GenericStateError(override val message: String) : RequestError

    data class BaseError(override val message: String) : RequestError
}