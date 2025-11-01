package com.github.radlance.autodispatch.request.presentation.create

import androidx.compose.ui.text.input.TextFieldValue
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateRequestViewModel : BaseViewModel(), EventViewModel<CreateRequestEvent> {
    private val fieldsUiStateMutable = MutableStateFlow(CreateRequestFieldsUiState())

    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

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