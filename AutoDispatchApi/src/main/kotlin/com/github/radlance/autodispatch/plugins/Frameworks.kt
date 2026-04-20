package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.deliveryModule
import com.github.radlance.autodispatch.di.documentModule
import com.github.radlance.autodispatch.di.driverModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import com.github.radlance.autodispatch.di.scheduleModule
import com.github.radlance.autodispatch.di.statisticsModule
import com.github.radlance.autodispatch.di.vehicleModule
import com.github.radlance.autodispatch.domain.request.EmailNotification
import com.github.radlance.autodispatch.service.MailService
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.basicConsume
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.exchangeDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueBind
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDi() {
    install(Koin) {
        slf4jLogger()
        modules(
            authModule,
            profileModule,
            scheduleModule,
            requestModule,
            deliveryModule,
            documentModule,
            driverModule,
            vehicleModule,
            statisticsModule
        )
    }
}

fun Application.configureBroker() {
    install(RabbitMQ) {
        uri = System.getenv("RABBIT_URI") ?: "amqp://guest:guest@localhost:5672"
        defaultConnectionName = "AutoDispatch-Service"
    }

    val mailService = get<MailService>()

    launch(Dispatchers.IO) {
        rabbitmq {
            queueBind {
                exchange = "dlx.emails"
                queue = "queue.emails.dlq"
                routingKey = "dead.letter"
                exchangeDeclare {
                    exchange = "dlx.emails"
                    type = "direct"
                    durable = true
                }
                queueDeclare {
                    queue = "queue.emails.dlq"
                    durable = true
                }
            }

            queueBind {
                exchange = "ex.notifications"
                queue = "queue.emails.main"
                routingKey = "send.email"

                exchangeDeclare {
                    exchange = "ex.notifications"
                    type = "direct"
                    durable = true
                }

                queueDeclare {
                    queue = "queue.emails.main"
                    durable = true
                    arguments = mapOf(
                        "x-dead-letter-exchange" to "dlx.emails",
                        "x-dead-letter-routing-key" to "dead.letter"
                    )
                }
            }

            basicConsume {
                queue = "queue.emails.main"
                autoAck = false

                channel.basicQos(1)

                deliverCallback { message ->
                    try {
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonString = String(message.body, Charsets.UTF_8)
                        val notification = json.decodeFromString<EmailNotification>(jsonString)

                        mailService.sendEmail(notification.email, notification.subject, notification.body)

                        channel.basicAck(message.envelope.deliveryTag, false)
                    } catch (e: Exception) {
                        log.error("Failed to process email notification", e)
                        channel.basicNack(message.envelope.deliveryTag, false, false)
                    }
                }
            }
        }
    }
}
