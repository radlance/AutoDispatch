package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.presentation.EmailTemplateBuilder
import com.github.radlance.autodispatch.repository.RequestRepository
import kotlinx.html.p

class RequestService(
    private val repository: RequestRepository,
    private val notificationPublisher: NotificationPublisher
) {

    private val testCustomerEmail = "manyakindima@outlook.com"
    private val testDriverEmail = "dmanyakin@yandex.ru"

    suspend fun getRequests(
        page: Int, pageSize: Int, searchQuery: String?,
        originCityIds: List<Int>, destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>, statusIds: List<Int>,
        driverIds: List<Int>, vehicleIds: List<Int>
    ) = repository.requests(
        page, pageSize, searchQuery,
        originCityIds, destinationCityIds,
        cargoTypeIds, statusIds, driverIds, vehicleIds
    )

    suspend fun getFilters() = repository.filters()
    suspend fun getCustomers(query: String) = repository.customers(query)
    suspend fun getAvailableRequests(page: Int, pageSize: Int, searchQuery: String?) =
        repository.availableRequests(page, pageSize, searchQuery)

    suspend fun removeRequest(requestId: Int) =
        repository.removeRequest(requestId)

    suspend fun createRequest(createdByLogin: String, request: CreateRequest) {
        val requestId = repository.createRequest(createdByLogin, request)
        val emailView = repository.getRequestEmailView(requestId) ?: return

        val customerEmailBody = EmailTemplateBuilder.customer(title = "Заявка успешно создана") {
            p { +"Ваша заявка №${emailView.requestNumber} успешно создана и принята к обработке." }

            emailView.transportationDescription?.let { desc ->
                p { +"Описание груза: $desc" }
            }

            emailView.cargoTypeName?.let { type ->
                p { +"Тип груза: $type" }
            }

            emailView.loadingAddress?.let { loading ->
                emailView.unloadingAddress?.let { unloading ->
                    p { +"Маршрут: $loading → $unloading" }
                }
            }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = "Ваша заявка №${emailView.requestNumber} создана",
                body = customerEmailBody
            )
        )
    }

    suspend fun editRequest(createdByLogin: String, requestId: Int, request: CreateRequest) {
        repository.editRequest(createdByLogin, requestId, request)
        val contacts = repository.getNotificationData(requestId) ?: return

        val customerEmail = EmailTemplateBuilder.customer(title = "Обновление заявки") {
            p { +"В заявку №${contacts.reqNumber} внесены изменения." }
            contacts.driverFullName?.let { p { +"Водитель: $it" } }
            contacts.driverPhoneNumber?.let { p { +"Телефон водителя: $it" } }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = "Заявка №${contacts.reqNumber} обновлена",
                body = customerEmail
            )
        )
    }

    suspend fun cancelAssignment(requestId: Int) {
        val contacts = repository.getNotificationData(requestId)
        repository.cancelAssignment(requestId)

        contacts?.let {
            notifyBoth(
                customerSubj = "Заявка №${it.reqNumber} отменена",
                customerBody = buildString {
                    append("Ваша заявка №${it.reqNumber} снята с выполнения.")
                    it.driverFullName?.let { fullName -> append("\nВодитель: $fullName") }
                    it.driverPhoneNumber?.let { phoneNumber -> append("\nТелефон водителя: $phoneNumber") }
                },
                driverSubj = "Снятие с заявки №${it.reqNumber}",
                driverBody = "Вы сняты с выполнения заявки."
            )
        }
    }

    suspend fun assignDriver(requestId: Int, driverId: Int) {
        repository.assignRequestToDriver(requestId, driverId)
        val contacts = repository.getNotificationData(requestId) ?: return

        notifyBoth(
            customerSubj = "Обновление статуса заявки №${contacts.reqNumber}",
            customerBody = "Заявка №${contacts.reqNumber} назначена водителю ${contacts.driverFullName} (${contacts.driverPhoneNumber}).",
            driverSubj = "Назначена новая заявка №${contacts.reqNumber}",
            driverBody = "Вам назначена заявка №${contacts.reqNumber}. Проверьте детали.",
            driverButtonUrl = "your-app://requests/$requestId"
        )
    }

    suspend fun reassignDriver(requestId: Int, driverId: Int) {
        repository.reassignRequestToDriver(requestId, driverId)
        notifyStatusChanged(requestId, "Заявка переназначена другому водителю")
    }

    suspend fun unassignDriver(requestId: Int) {
        val contacts = repository.getNotificationData(requestId)
        repository.unassignDriver(requestId)

        contacts?.let {
            notifyBoth(
                customerSubj = "Заявка №${it.reqNumber} возвращена в ожидание",
                customerBody = buildString {
                    append("Заявка №${it.reqNumber} больше не назначена водителю.")
                    it.driverFullName?.let { fullName -> append("\nБывший водитель: $fullName") }
                    it.driverPhoneNumber?.let { phoneNumber -> append("\nТелефон: $phoneNumber") }
                },
                driverSubj = "Снятие с заявки №${it.reqNumber}",
                driverBody = "Вы сняты с выполнения заявки."
            )
        }
    }

    private suspend fun notifyStatusChanged(requestId: Int, statusDescription: String) {
        val contacts = repository.getNotificationData(requestId) ?: return
        notifyBoth(
            customerSubj = "Обновление статуса заявки №${contacts.reqNumber}",
            customerBody = buildString {
                append("Статус заявки №${contacts.reqNumber} изменился: $statusDescription")
                contacts.driverFullName?.let { append("\nВодитель: $it") }
                contacts.driverPhoneNumber?.let { append("\nТелефон водителя: $it") }
            },
            driverSubj = "Обновление статуса заявки №${contacts.reqNumber}",
            driverBody = "Статус заявки изменился: $statusDescription"
        )
    }

    private suspend fun notifyBoth(
        customerSubj: String,
        customerBody: String,
        driverSubj: String,
        driverBody: String,
        driverButtonUrl: String? = null
    ) {
        val customerEmail = EmailTemplateBuilder.customer(title = "AutoDispatch") {
            p { +customerBody }
        }

        val driverEmail = EmailTemplateBuilder.driver(
            title = "AutoDispatch",
            buttonUrl = driverButtonUrl
        ) {
            p { +driverBody }
        }

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = customerSubj,
                body = customerEmail
            )
        )

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testDriverEmail,
                subject = driverSubj,
                body = driverEmail
            )
        )
    }
}
