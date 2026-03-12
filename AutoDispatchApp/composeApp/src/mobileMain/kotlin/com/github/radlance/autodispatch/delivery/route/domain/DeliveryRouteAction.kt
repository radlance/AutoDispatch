package com.github.radlance.autodispatch.delivery.route.domain

sealed interface DeliveryRouteAction {
    data object ArriveLoading : DeliveryRouteAction
    data object DepartLoading : DeliveryRouteAction
    data object ArriveUnloading : DeliveryRouteAction
}
