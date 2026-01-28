package com.github.radlance.autodispatch.service

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.basicPublish
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface NotificationPublisher {
    suspend fun publish(event: NotificationEvent)
}

class KtorRabbitNotificationPublisher(
    private val application: Application
) : NotificationPublisher {

    private val publisherScope = CoroutineScope(Dispatchers.IO)

    override suspend fun publish(event: NotificationEvent) {
        publisherScope.launch {
            try {
                application.publishNotification(event)
            } catch (e: Exception) {
                application.log.error("[RabbitMQ] Background publish failed: ${e.message}")
            }
        }
    }
}

private fun Application.publishNotification(event: NotificationEvent) {
    rabbitmq {
        basicPublish {
            exchange = "ex.notifications"
            routingKey = when (event) {
                is RequestStatusChangedEvent -> "request.status.changed"
                is DriverStatusEvent -> "driver.status.changed"
                is EmailNotificationEvent -> "send.email"
            }
            message { event }
        }
    }
}

@Serializable
sealed interface NotificationEvent

@Serializable
data class EmailNotificationEvent(
    val email: String,
    val subject: String,
    val body: String
) : NotificationEvent

@Serializable
data class RequestStatusChangedEvent(
    val requestId: Int,
    val newStatus: String,
    val customerEmail: String
) : NotificationEvent

@Serializable
data class DriverStatusEvent(
    val requestId: Int,
    val driverEmail: String,
    val status: String
) : NotificationEvent
