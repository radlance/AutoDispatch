package com.github.radlance.autodispatch.exception

data class MissingCredentialException(override val message: String = "Missing credentials") : RuntimeException()

data class DeliveryNotFoundException(override val message: String = "This delivery does not exist") : RuntimeException()

data class DeliveryForbiddenException(override val message: String = "Delivery access denied") : RuntimeException()

data class StateConflictException(override val message: String = "Illegal delivery state") : RuntimeException()

data class DriverBusyException(override val message: String) : RuntimeException(message)

class DeliveryCanceledException(override val message: String) : RuntimeException()