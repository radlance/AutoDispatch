package com.github.radlance.autodispatch.delivery.route.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.delivery.route.domain.DeliveryRouteRepository
import com.github.radlance.autodispatch.delivery.route.domain.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeliveryRouteViewModel(
    private val repository: DeliveryRouteRepository
) : BaseViewModel() {
    private val currentLocationMutable = MutableStateFlow<Location?>(null)
    val currentLocation = currentLocationMutable.asStateFlow()

    fun fetchCurrentLocation() {

        handle(background = repository::getCurrentLocation) {
            currentLocationMutable.value = it
        }
    }
}