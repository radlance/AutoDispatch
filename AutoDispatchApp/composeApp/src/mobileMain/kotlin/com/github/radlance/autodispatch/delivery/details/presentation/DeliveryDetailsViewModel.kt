package com.github.radlance.autodispatch.delivery.details.presentation

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.delivery.route.domain.DeliveryRouteAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DeliveryDetailsViewModel(
    private val repository: DeliveryDetailsRepository
) : BaseViewModel() {
    private val deliveryStateMutable =
        MutableStateFlow<FetchResultUiState<DeliveryDetailed, RequestError>>(FetchResultUiState.Idle)
    val deliveryState = deliveryStateMutable.asStateFlow()

    private val acceptDeliveryStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val acceptDeliveryState = acceptDeliveryStateMutable.asStateFlow()

    private val routeActionStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val routeActionState = routeActionStateMutable.asStateFlow()

    private val routeActionTypeMutable = MutableStateFlow<DeliveryRouteAction?>(null)
    val routeActionType = routeActionTypeMutable.asStateFlow()

    private var deliveryJob: Job? = null

    fun fetchDeliveryDetails(deliveryId: Int) {
        deliveryJob?.cancel()
        deliveryStateMutable.value = FetchResultUiState.Loading
        deliveryJob = handle(background = { repository.deliveryDetails(deliveryId) }) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    @OptIn(ExperimentalTime::class)
    fun acceptDelivery(deliveryId: Int) {
        acceptDeliveryStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.acceptDelivery(deliveryId) }) { result ->
            acceptDeliveryStateMutable.value = result.toUiState()

            if (result is FetchResult.Success) {
                val current = deliveryStateMutable.value
                if (current is FetchResultUiState.Success) {
                    val updatedDelivery = current.data.copy(
                        status = RequestStatus.InProgress,
                        updatedAt = Clock.System.now().toLocalDateTime(
                            TimeZone.currentSystemDefault()
                        )
                    )
                    deliveryStateMutable.value = FetchResultUiState.Success(updatedDelivery)
                }
            }
        }
    }


    fun resetAcceptState() {
        acceptDeliveryStateMutable.value = FetchResultUiState.Idle
    }

    private val detourSheetStateMutable =
        MutableStateFlow<FetchResultUiState<ByteArray, RequestError>>(FetchResultUiState.Idle)
    val detourSheetState = detourSheetStateMutable.asStateFlow()

    fun downloadDetourSheet(deliveryId: Int) {
        detourSheetStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.detourSheet(deliveryId) }) { result ->
            detourSheetStateMutable.value = result.toUiState()
        }
    }

    fun resetDetourSheetState() {
        detourSheetStateMutable.value = FetchResultUiState.Idle
    }

    fun arriveLoading(deliveryId: Int) {
        updateRouteProgress(
            action = DeliveryRouteAction.ArriveLoading,
            request = { repository.arriveLoading(deliveryId) },
            applyUpdate = { current, now ->
                if (current.arrivedLoadingAt != null) current
                else current.copy(arrivedLoadingAt = now, updatedAt = now)
            }
        )
    }

    fun arriveUnloading(deliveryId: Int) {
        updateRouteProgress(
            action = DeliveryRouteAction.ArriveUnloading,
            request = { repository.arriveUnloading(deliveryId) },
            applyUpdate = { current, now ->
                if (current.arrivedUnloadingAt != null) current
                else current.copy(arrivedUnloadingAt = now, updatedAt = now)
            }
        )
    }

    fun resetRouteActionState() {
        routeActionStateMutable.value = FetchResultUiState.Idle
        routeActionTypeMutable.value = null
    }

    @OptIn(ExperimentalTime::class)
    private fun updateRouteProgress(
        action: DeliveryRouteAction,
        request: suspend () -> FetchResult<Unit, RequestError>,
        applyUpdate: (DeliveryDetailed, kotlinx.datetime.LocalDateTime) -> DeliveryDetailed
    ) {
        routeActionTypeMutable.value = action
        routeActionStateMutable.value = FetchResultUiState.Loading

        handle(background = { request() }) { result ->
            routeActionStateMutable.value = result.toUiState()

            if (result is FetchResult.Success) {
                val current = deliveryStateMutable.value
                if (current is FetchResultUiState.Success) {
                    val now = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    val updatedDelivery = applyUpdate(current.data, now)
                    deliveryStateMutable.value = FetchResultUiState.Success(updatedDelivery)
                }
            }
        }
    }
}