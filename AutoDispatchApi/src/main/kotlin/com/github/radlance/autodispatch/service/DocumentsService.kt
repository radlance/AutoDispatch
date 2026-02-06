package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.document.RejectDocumentDto
import com.github.radlance.autodispatch.presentation.EmailTemplateBuilder
import com.github.radlance.autodispatch.repository.DocumentsRepository
import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.repository.RequestRepository
import kotlinx.html.p

class DocumentsService(
    private val documentsRepository: DocumentsRepository,
    private val deliveryRepository: DeliveryRepository,
    private val requestRepository: RequestRepository,
    private val notificationPublisher: NotificationPublisher
) {
    private val testCustomerEmail = "manyakindima@outlook.com"
    private val testDriverEmail = "dmanyakin@yandex.ru"

    suspend fun approveDocuments(requestId: Int) {
        documentsRepository.approveDocument(requestId)

        val contacts = requestRepository.getNotificationData(requestId) ?: return

        val driverLogin = contacts.driverLogin ?: return

        val documents = deliveryRepository.delivery(
            driverLogin = driverLogin,
            deliveryId = requestId
        ).documents

        val docUrls = documents.map { it.imageUrl }

        val customerBody = EmailTemplateBuilder.customer(
            title = "Доставка завершена"
        ) {
            p { +"Доставка по заявке №${contacts.reqNumber} успешно завершена." }
            contacts.driverFullName?.let { p { +"Водитель: $it" } }
            if (docUrls.isNotEmpty()) {
                p { +"Документы:" }
                docUrls.forEach { url -> p { +url } }
            }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = "Заявка №${contacts.reqNumber} доставлена",
                body = customerBody
            )
        )

        val driverBody = EmailTemplateBuilder.driver(
            title = "Доставка завершена"
        ) {
            p { +"Вы успешно завершили доставку по заявке №${contacts.reqNumber}." }
            if (docUrls.isNotEmpty()) {
                p { +"Документы:" }
                docUrls.forEach { url -> p { +url } }
            }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testDriverEmail,
                subject = "Доставка №${contacts.reqNumber} завершена",
                body = driverBody
            )
        )
    }

    suspend fun rejectDocuments(requestId: Int, reason: String) {
        documentsRepository.rejectDocument(
            requestId = requestId,
            rejectDocumentDto = RejectDocumentDto(reason)
        )

        val contacts = requestRepository.getNotificationData(requestId) ?: return

        val body = EmailTemplateBuilder.driver(
            title = "Документы отклонены"
        ) {
            p { +"Документы по заявке №${contacts.reqNumber} отклонены." }
            p { +"Причина: $reason" }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testDriverEmail,
                subject = "Документы по заявке №${contacts.reqNumber} отклонены",
                body = body
            )
        )
    }
}
