package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.repository.RequestRepository

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

        val description = emailView.transportationDescription
            ?: emailView.cargoDescription
            ?: "Не указано"

        val cargoType = emailView.cargoTypeName ?: "Не указано"
        val loading = emailView.loadingAddress ?: "Не указано"
        val unloading = emailView.unloadingAddress ?: "Не указано"

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = "Ваша заявка создана",
                body = buildCustomerHtml(
                    title = "Заявка успешно создана",
                    message = """
                        Здравствуйте! Ваша заявка №${emailView.requestNumber} создана и готова к обработке.
                        <br/><br/>
                        <strong>Описание груза:</strong> $description<br/>
                        <strong>Тип груза:</strong> $cargoType<br/>
                        <strong>Маршрут:</strong> $loading → $unloading
                    """.trimIndent(),
                    extraInfo = """
                        <a href="https://your-customer-dashboard.com/requests/${emailView.requestId}"
                           class="button">Просмотреть заявку</a>
                    """
                )
            )
        )
    }

    suspend fun editRequest(createdByLogin: String, requestId: Int, request: CreateRequest) {
        repository.editRequest(createdByLogin, requestId, request)
        val contacts = repository.getNotificationData(requestId) ?: return

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = "Заявка №${contacts.reqNumber} обновлена",
                body = buildCustomerHtml(
                    title = "Обновление заявки",
                    message = "Мы внесли изменения в заявку №${contacts.reqNumber}. Проверьте детали в личном кабинете."
                )
            )
        )
    }

    suspend fun cancelAssignment(requestId: Int) {
        val contacts = repository.getNotificationData(requestId)
        repository.cancelAssignment(requestId)

        contacts?.let {
            notifyBoth(
                customerSubj = "Заявка №${it.reqNumber} отменена",
                customerBody = "Ваша заявка снята с выполнения.",
                driverSubj = "Снятие с заявки №${it.reqNumber}",
                driverBody = "Вы сняты с выполнения заявки."
            )
        }
    }

    suspend fun assignDriver(requestId: Int, driverId: Int) {
        repository.assignRequestToDriver(requestId, driverId)
        notifyStatusChanged(
            requestId,
            driverSubj = "Новая заявка назначена",
            customerSubj = "Заявка назначена водителю"
        )
    }

    suspend fun reassignDriver(requestId: Int, driverId: Int) {
        repository.reassignRequestToDriver(requestId, driverId)
        notifyStatusChanged(
            requestId,
            driverSubj = "Заявка переназначена",
            customerSubj = "Заявка переназначена другому водителю"
        )
    }

    suspend fun unassignDriver(requestId: Int) {
        val contacts = repository.getNotificationData(requestId)
        repository.unassignDriver(requestId)

        contacts?.let {
            notifyBoth(
                customerSubj = "Заявка №${it.reqNumber} возвращена в ожидание",
                customerBody = "Мы подберем водителя в ближайшее время.",
                driverSubj = "Снятие с заявки №${it.reqNumber}",
                driverBody = "Вы сняты с выполнения заявки."
            )
        }
    }

    private suspend fun notifyStatusChanged(
        requestId: Int,
        driverSubj: String,
        customerSubj: String
    ) {
        val contacts = repository.getNotificationData(requestId) ?: return
        notifyBoth(
            customerSubj,
            "Статус заявки №${contacts.reqNumber} обновлен.",
            driverSubj,
            "Проверьте заявку №${contacts.reqNumber}."
        )
    }

    private suspend fun notifyBoth(
        customerSubj: String,
        customerBody: String,
        driverSubj: String,
        driverBody: String
    ) {
        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testCustomerEmail,
                subject = customerSubj,
                body = buildCustomerHtml("AutoDispatch", customerBody)
            )
        )

        notificationPublisher.publish(
            EmailNotificationEvent(
                email = testDriverEmail,
                subject = driverSubj,
                body = buildDriverHtml("AutoDispatch", driverBody)
            )
        )
    }

    private fun buildCustomerHtml(title: String, message: String, extraInfo: String? = null): String = """
        <html>
        <head>
            <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
            <style>
                body { font-family: 'Roboto', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                .header { background-color: #6200EE; padding: 24px; text-align: center; color: #ffffff; }
                .header h1 { font-size: 24px; font-weight: 500; margin: 0; }
                .content { padding: 32px 24px; color: #212121; }
                .content p { font-size: 16px; line-height: 24px; margin: 0 0 16px; }
                .footer { background-color: #f4f4f4; padding: 16px; text-align: center; font-size: 12px; color: #757575; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>$title</h1>
                </div>
                <div class="content">
                    <p>$message</p>
                    ${extraInfo ?: ""}
                </div>
                <div class="footer">
                    Это автоматическое сообщение от AutoDispatch. Пожалуйста, не отвечайте на него.
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()

    private fun buildDriverHtml(title: String, message: String): String = """
        <html>
        <head>
            <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
            <style>
                body { font-family: 'Roboto', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                .header { background-color: #03DAC6; padding: 24px; text-align: center; color: #000000; }
                .header h1 { font-size: 24px; font-weight: 500; margin: 0; }
                .content { padding: 32px 24px; color: #212121; }
                .content p { font-size: 16px; line-height: 24px; margin: 0 0 16px; }
                .button { display: inline-block; background-color: #03DAC6; color: #000000; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: 500; font-size: 14px; }
                .content .button-wrapper { text-align: center; margin-top: 24px; }
                .footer { background-color: #f4f4f4; padding: 16px; text-align: center; font-size: 12px; color: #757575; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>$title</h1>
                </div>
                <div class="content">
                    <p>$message</p>
                    <div class="button-wrapper">
                        <a href="#" class="button">Открыть заявку</a>
                    </div>
                </div>
                <div class="footer">
                    Это автоматическое сообщение от AutoDispatch. Пожалуйста, не отвечайте на него.
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()
}