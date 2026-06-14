package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.di.*
import com.github.radlance.autodispatch.domain.request.EmailNotification
import com.github.radlance.autodispatch.service.MailService
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import io.opentelemetry.semconv.ServiceAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.time.Instant

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
            statisticsModule,
            adminModule
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

fun Application.configureMonitoring() {
    val serviceName = environment.config.propertyOrNull("ktor.application.id")?.getString() ?: "AutoDispatchApi"

    val openTelemetry = getOpenTelemetry(serviceName)

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
        knownMethods(HttpMethod.DefaultMethods)
        capturedRequestHeaders(HttpHeaders.UserAgent)
        spanKindExtractor {
            if (httpMethod == HttpMethod.Post) {
                SpanKind.PRODUCER
            } else {
                SpanKind.CLIENT
            }
        }
        attributesExtractor {
            onStart {
                attributes.put("start-time", System.currentTimeMillis())
            }
            onEnd {
                attributes.put("end-time", Instant.now().toEpochMilli())
            }
        }
    }
}

fun getOpenTelemetry(serviceName: String): OpenTelemetry {

    return AutoConfiguredOpenTelemetrySdk.builder().addResourceCustomizer { oldResource, _ ->
        val customResource = io.opentelemetry.sdk.resources.Resource.builder()
            .put(ServiceAttributes.SERVICE_NAME, serviceName)
            .put(io.opentelemetry.api.common.AttributeKey.stringKey("service.instance.id"), serviceName)
            .build()
        
        oldResource.merge(customResource)
    }.build().openTelemetrySdk
}