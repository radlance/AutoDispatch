package com.github.radlance.autodispatch.delivery.details.presentation

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.domain.RequestError
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
}