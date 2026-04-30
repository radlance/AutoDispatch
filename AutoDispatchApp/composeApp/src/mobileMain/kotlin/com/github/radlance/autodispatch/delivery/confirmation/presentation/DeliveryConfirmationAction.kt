package com.github.radlance.autodispatch.delivery.confirmation.presentation

import com.github.radlance.autodispatch.request.core.domain.DocumentType
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.common.utils.toStringAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DeliveryConfirmationAction {
    val title: String
    val locationTitle: String
    val warningMessage: String

    fun getAddress(delivery: DeliveryDetailed): String

    fun action(
        viewModel: DeliveryConfirmationViewModel,
        deliveryId: Int,
        documents: List<ByteArray>
    )

    @Serializable
    @SerialName("shipment")
    data object Shipment : DeliveryConfirmationAction {
        override val title: String = "Подтверждение"
        override val locationTitle: String = "Точка погрузки"
        override val warningMessage: String = "Сделайте фото документов об отгрузке. Фотография должна быть чёткой и читаемой."

        override fun getAddress(delivery: DeliveryDetailed): String {
            return delivery.loadingPoint.toStringAddress()
        }

        override fun action(
            viewModel: DeliveryConfirmationViewModel,
            deliveryId: Int,
            documents: List<ByteArray>
        ) {
            viewModel.shipDocuments(deliveryId, documents)
        }
    }

    @Serializable
    @SerialName("retake")
    class Retake(
        @SerialName("document_confirm_type")
        val documentType: DocumentType,
    ) : DeliveryConfirmationAction {
        override val title: String = "Повторная отправка"
        override val locationTitle: String = if (documentType == DocumentType.SHIPPING) "Точка погрузки" else "Точка назначения"
        override val warningMessage: String = "Пересдайте фото документов. Фотография должна быть чёткой и читаемой."

        override fun getAddress(delivery: DeliveryDetailed): String {
            return if (documentType == DocumentType.SHIPPING) {
                delivery.loadingPoint.toStringAddress()
            } else {
                delivery.unloadingPoint.toStringAddress()
            }
        }

        override fun action(
            viewModel: DeliveryConfirmationViewModel,
            deliveryId: Int,
            documents: List<ByteArray>
        ) {
            viewModel.retakeDocument(deliveryId, documents, documentType)
        }
    }

    @Serializable
    @SerialName("acceptance")
    data object Acceptance : DeliveryConfirmationAction {
        override val title: String = "Подтверждение"
        override val locationTitle: String = "Точка назначения"
        override val warningMessage: String = "Сделайте фото документа о доставке (накладная, ТТН). Фотография должна быть чёткой и читаемой."

        override fun getAddress(delivery: DeliveryDetailed): String {
            return delivery.unloadingPoint.toStringAddress()
        }

        override fun action(
            viewModel: DeliveryConfirmationViewModel,
            deliveryId: Int,
            documents: List<ByteArray>
        ) {
            viewModel.acceptDelivery(deliveryId, documents)
        }
    }
}