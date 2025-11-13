package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.delivery.Delivery
import com.github.radlance.autodispatch.domain.delivery.DeliveryDetailed
import com.github.radlance.autodispatch.repository.DeliveryRepository

class DeliveryService(private val deliveryRepository: DeliveryRepository) {
    suspend fun deliveries(driverLogin: String): List<Delivery> {
        return deliveryRepository.deliveries(driverLogin = driverLogin)
    }

    suspend fun delivery(driverLogin: String, deliveryId: Int): DeliveryDetailed {
        return deliveryRepository.delivery(driverLogin, deliveryId)
    }

    suspend fun startDelivery(deliveryId: Int, driverLogin: String) {
        deliveryRepository.startDelivery(deliveryId, driverLogin)
    }
}