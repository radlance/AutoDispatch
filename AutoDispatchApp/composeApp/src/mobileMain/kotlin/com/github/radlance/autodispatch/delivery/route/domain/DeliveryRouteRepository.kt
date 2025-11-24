package com.github.radlance.autodispatch.delivery.route.domain

interface DeliveryRouteRepository {

    suspend fun getCurrentLocation(): Location?
}