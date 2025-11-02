package com.github.radlance.autodispatch.request.create.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.create.domain.CreateRequest
import com.github.radlance.autodispatch.request.create.domain.CreateRequestRepository
import com.github.radlance.autodispatch.request.create.domain.Customer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateRequestViewModel(
    private val repository: CreateRequestRepository,
    private val validator: RequestValidator
) : BaseViewModel(), EventViewModel<CreateRequestEvent> {
    private val fieldsUiStateMutable = MutableStateFlow(CreateRequestFieldsUiState())
    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    private val customersStateMutable = MutableStateFlow<List<Customer>>(emptyList())
    val customersState get() = customersStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 300L

    private val createRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)

    val createRequestState = createRequestStateMutable.asStateFlow()

    override fun reduce(event: CreateRequestEvent) {
        val action = object : CreateRequestAction {
            override fun changeDepartureCity(city: City) {
                fieldsUiStateMutable.update { state ->
                    state.copy(departureCity = city)
                }
            }

            override fun changeDestinationCity(city: City) {
                fieldsUiStateMutable.update { state ->
                    state.copy(destinationCity = city)
                }
            }

            override fun changeCargoType(cargoType: CargoType) {
                fieldsUiStateMutable.update { state ->
                    state.copy(cargoType = cargoType)
                }
            }

            override fun changeCompanyName(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(companyNameFieldValue = value)
                }

                searchJob?.cancel()

                if (value.isEmpty()) {
                    customersStateMutable.value = emptyList()
                    return
                }

                searchJob = viewModelScope.launch {
                    delay(debounceTime)
                    handle(background = { repository.customers(value) }) {
                        customersStateMutable.value = it
                    }
                }
            }

            override fun changeCompanyEmail(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(companyEmailFieldValue = value, companyEmailErrorMessage = "")
                }
            }

            override fun changeCompanyPhone(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(companyPhoneFieldValue = value, companyPhoneErrorMessage = "")
                }
            }

            override fun changeCargoWeight(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(cargoWeightFieldValue = value, cargoWeightErrorMessage = "")
                }
            }

            override fun changCargoVolume(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(cargoVolumeFieldValue = value, cargoVolumeErrorMessage = "")
                }
            }

            override fun changeCargoDescription(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(cargoDescriptionFieldValue = value)
                }
            }

            override fun changeLoading(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(loadingFieldValue = value)
                }
            }

            override fun changeUnloading(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(unloadingFieldValue = value)
                }
            }

            override fun changeAdditionalInfo(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(additionalInfoFieldValue = value)
                }
            }

            override fun createRequest(
                originId: Int,
                destinationId: Int,
                companyName: String,
                companyEmail: String,
                companyPhone: String?,
                cargoTypeId: Int,
                cargoWeight: String,
                cargoVolume: String?,
                cargoDescription: String?,
                cargoLoading: String,
                cargoUnloading: String,
                additionalInfo: String?,
                requestId: Int?
            ) {
                with(validator) {
                    fieldsUiStateMutable.update { state ->
                        state.copy(
                            companyEmailErrorMessage = validationEmailMessage(companyEmail),
                            companyPhoneErrorMessage = companyPhone?.let {
                                validationPhoneNumberMessage(
                                    it
                                )
                            }
                                ?: "",
                            cargoWeightErrorMessage = validationWeightMessage(cargoWeight),
                            cargoVolumeErrorMessage = cargoVolume?.let { validationVolumeMessage(it) }
                                ?: ""
                        )
                    }
                }

                with(fieldsUiStateMutable.value) {
                    if (
                        companyEmailErrorMessage.isEmpty()
                        && companyPhoneErrorMessage.isEmpty()
                        && cargoWeightErrorMessage.isEmpty()
                        && cargoVolumeErrorMessage.isEmpty()
                    ) {
                        createRequestStateMutable.value = FetchResultUiState.Loading


                        handle(
                            background = {
                                val request = CreateRequest(
                                    loadingPoint = cargoLoading,
                                    unloadingPoint = cargoUnloading,
                                    cargoTypeId = cargoTypeId,
                                    cargoWeight = cargoWeight.toDouble(),
                                    cargoVolume = cargoVolume?.toDouble(),
                                    cargoDescription = cargoDescription,
                                    customerName = companyName,
                                    customerEmail = companyEmail,
                                    customerPhone = companyPhone,
                                    originId = originId,
                                    destinationId = destinationId,
                                    transportationDescription = additionalInfo
                                )
                                requestId?.let {
                                    repository.editRequest(requestId, request)
                                } ?: repository.createRequest(request)
                            }
                        ) {
                            createRequestStateMutable.value = it.toUiState()
                        }
                    }
                }

            }

            override fun resetState() {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        departureCity = null,
                        destinationCity = null,
                        cargoType = null,
                        companyNameFieldValue = "",
                        companyEmailFieldValue = "",
                        companyEmailErrorMessage = "",
                        companyPhoneFieldValue = "",
                        companyPhoneErrorMessage = "",
                        cargoWeightFieldValue = "",
                        cargoWeightErrorMessage = "",
                        cargoVolumeFieldValue = "",
                        cargoVolumeErrorMessage = "",
                        cargoDescriptionFieldValue = "",
                        loadingFieldValue = "",
                        unloadingFieldValue = "",
                        additionalInfoFieldValue = ""
                    )
                }

                createRequestStateMutable.value = FetchResultUiState.Idle
            }

            override fun setupRequestFieldsState(fieldsUiState: CreateRequestFieldsUiState) {
                fieldsUiStateMutable.value = fieldsUiState
            }
        }
        event.apply(action = action)
    }
}