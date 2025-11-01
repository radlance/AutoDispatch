package com.github.radlance.autodispatch.request.create.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.create.domain.CreateRequestRepository
import com.github.radlance.autodispatch.request.create.domain.Customer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateRequestViewModel(
    private val repository: CreateRequestRepository
) : BaseViewModel(), EventViewModel<CreateRequestEvent> {
    private val fieldsUiStateMutable = MutableStateFlow(CreateRequestFieldsUiState())
    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    private val customersStateMutable = MutableStateFlow<List<Customer>>(emptyList())
    val customersState get() = customersStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 300L

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

            override fun changeCompanyName(value: TextFieldValue) {
                fieldsUiStateMutable.update { state ->
                    state.copy(companyNameFieldValue = value)
                }

                searchJob?.cancel()

                if (value.text.isEmpty()) {
                    customersStateMutable.value = emptyList()
                    return
                }

                searchJob = viewModelScope.launch {
                    delay(debounceTime)
                    handle(background = { repository.customers(value.text) }) {
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
                companyName: String,
                companyEmail: String,
                companyPhone: String?,
                cargoWeight: String,
                cargoVolume: String?,
                cargoDescription: String?,
                cargoLoading: String,
                cargoUnloading: String,
                additionalInfo: String?
            ) {
                TODO("Not yet implemented")
            }
        }
        event.apply(action = action)
    }
}