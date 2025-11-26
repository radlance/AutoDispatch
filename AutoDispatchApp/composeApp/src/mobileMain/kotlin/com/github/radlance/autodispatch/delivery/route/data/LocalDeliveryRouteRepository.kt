package com.github.radlance.autodispatch.delivery.route.data

import com.github.radlance.autodispatch.delivery.route.domain.DeliveryRouteRepository
import com.github.radlance.autodispatch.delivery.route.domain.Location
import com.github.radlance.autodispatch.platform.getCurrentLocation

class LocalDeliveryRouteRepository(private val context: Any?) : DeliveryRouteRepository {

    override suspend fun getCurrentLocation(): Location? {
        return getCurrentLocation(context)
    }
}