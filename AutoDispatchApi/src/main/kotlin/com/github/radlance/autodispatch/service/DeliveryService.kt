package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.history.DriverHistory
import com.github.radlance.autodispatch.presentation.EmailTemplateBuilder
import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.repository.RequestRepository
import kotlinx.html.p

class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val requestRepository: RequestRepository,
    private val notificationPublisher: NotificationPublisher
) {

    private val testCustomerEmail = "manyakindima@outlook.com"

    suspend fun startDelivery(deliveryId: Int, driverLogin: String) {
        deliveryRepository.startDelivery(deliveryId, driverLogin)

        val contacts = requestRepository.getNotificationData(deliveryId) ?: return

        val email = testCustomerEmail
        val body = EmailTemplateBuilder.customer(
            title = "Доставка началась"
        ) {
            p { +"По заявке №${contacts.reqNumber} водитель приступил к доставке." }
            contacts.driverFullName?.let { p { +"Водитель: $it" } }
            contacts.driverPhoneNumber?.let { p { +"Телефон водителя: $it" } }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = email,
                subject = "Доставка по заявке №${contacts.reqNumber} началась",
                body = body
            )
        )
    }

    suspend fun deliveries(
        driverLogin: String,
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ) = deliveryRepository.deliveries(
        driverLogin = driverLogin,
        searchQuery = searchQuery,
        page = page,
        pageSie = pageSize
    )

    suspend fun delivery(driverLogin: String, deliveryId: Int) =
        deliveryRepository.delivery(driverLogin, deliveryId)

    suspend fun uploadDocuments(
        deliveryId: Int,
        driverLogin: String,
        imageUrls: List<String>
    ) = deliveryRepository.uploadAcceptanceDocuments(
        deliveryId,
        driverLogin,
        imageUrls
    )

    suspend fun uploadShippingDocuments(
        deliveryId: Int,
        driverLogin: String,
        imageUrls: List<String>
    ) = deliveryRepository.uploadShippingDocuments(
        deliveryId,
        driverLogin,
        imageUrls
    )

    suspend fun arriveLoading(deliveryId: Int, driverLogin: String) =
        deliveryRepository.arriveLoading(deliveryId, driverLogin)

    suspend fun departLoading(deliveryId: Int, driverLogin: String) =
        deliveryRepository.departLoading(deliveryId, driverLogin)

    suspend fun arriveUnloading(deliveryId: Int, driverLogin: String) =
        deliveryRepository.arriveUnloading(deliveryId, driverLogin)

    suspend fun retakeDocuments(
        deliveryId: Int,
        driverLogin: String,
        imageUrls: List<String>,
        documentTypeId: Int
    ) = deliveryRepository.retakeDeliveryDocuments(
        deliveryId,
        driverLogin,
        imageUrls,
        documentTypeId
    )

    suspend fun deliveryHistory(
        driverLogin: String,
        searchQuery: String?,
        pageSize: Int,
        page: Int
    ) = deliveryRepository.deliveryHistory(
        driverLogin = driverLogin,
        searchQuery = searchQuery,
        pageSize = pageSize,
        page = page
    )
    
    suspend fun driverDeliveryHistory(
        driverId: Int,
        searchQuery: String?,
        pageSize: Int,
        page: Int
    ): ListPaginatedResult<DriverHistory> {
        return deliveryRepository.driverDeliveryHistory(
            driverId = driverId,
            searchQuery = searchQuery,
            pageSize = pageSize,
            page = page
        )
    }
}
