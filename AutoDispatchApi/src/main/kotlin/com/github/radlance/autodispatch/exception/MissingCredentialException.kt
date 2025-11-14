package com.github.radlance.autodispatch.exception

data class MissingCredentialException(override val message: String = "Missing credentials") : RuntimeException()

data class DeliveryNotFoundException(override val message: String = "This delivery does not exist") : RuntimeException()

data class DeliveryForbiddenException(override val message: String = "Delivery access denied") : RuntimeException()

data class DeliveryStateException(override val message: String = "Illegal delivery state") : RuntimeException()

class DriverBusyException : RuntimeException("Вы уже выполняете другую доставку. Завершите ее, прежде чем начинать новую.")

class DeliveryCanceledException(requestNumber: String) : RuntimeException("Доставка $requestNumber отменена")