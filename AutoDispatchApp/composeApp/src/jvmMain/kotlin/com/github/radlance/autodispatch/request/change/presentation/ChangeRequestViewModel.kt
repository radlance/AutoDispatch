package com.github.radlance.autodispatch.request.change.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.change.domain.ChangeRequestRepository
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeRequestViewModel(
    private val repository: ChangeRequestRepository,
    private val validator: RequestValidator
) : BaseViewModel(), EventViewModel<ChangeRequestEvent> {
    private val fieldsUiStateMutable = MutableStateFlow(ChangeRequestFieldsUiState())
    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    private val customersStateMutable = MutableStateFlow<List<Customer>>(emptyList())
    val customersState get() = customersStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 300L

    private val changeRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val changeRequestState = changeRequestStateMutable.asStateFlow()

    private val cancelRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val cancelRequestState = cancelRequestStateMutable.asStateFlow()

    private val rejectDocumentsStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val rejectDocumentsState get() = rejectDocumentsStateMutable.asStateFlow()

    private val approveDocumentsStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val approveDocumentsState get() = approveDocumentsStateMutable.asStateFlow()

    override fun reduce(event: ChangeRequestEvent) {
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

            override fun changeLoading(value: Point?) {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        loadingFieldAddressValue = value?.toStringAddress() ?: "",
                        loadingFieldLatValue = value?.lat,
                        loadingFieldLonValue = value?.lon
                    )
                }
            }

            override fun changeUnloading(value: Point?) {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        unloadingFieldAddressValue = value?.toStringAddress() ?: "",
                        unloadingFieldLatValue = value?.lat,
                        unloadingFieldLonValue = value?.lon
                    )
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
                cargoLoadingAddress: String?,
                cargoLoadingLon: Double,
                cargoLoadingLat: Double,
                cargoUnloadingAddress: String?,
                cargoUnloadingLon: Double,
                cargoUnloadingLat: Double,
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
                        changeRequestStateMutable.value = FetchResultUiState.Loading


                        handle(
                            background = {
                                val request = ChangeRequest(
                                    loadingAddress = cargoLoadingAddress,
                                    loadingLon = cargoLoadingLon,
                                    loadingLat = cargoLoadingLat,
                                    unloadingAddress = cargoUnloadingAddress,
                                    unloadingLon = cargoUnloadingLon,
                                    unloadingLat = cargoUnloadingLat,
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
                            changeRequestStateMutable.value = it.toUiState()
                        }
                    }
                }

            }

            override fun resetChangeState() {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        requestId = null,
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
                        loadingFieldAddressValue = "",
                        loadingFieldLatValue = null,
                        loadingFieldLonValue = null,
                        unloadingFieldAddressValue = "",
                        unloadingFieldLatValue = null,
                        unloadingFieldLonValue = null,
                        additionalInfoFieldValue = ""
                    )
                }

                changeRequestStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetRemoveState() {
                cancelRequestStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetRejectState() {
                rejectDocumentsStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetApproveState() {
                approveDocumentsStateMutable.value = FetchResultUiState.Idle
            }

            override fun setupRequestFieldsState(fieldsUiState: ChangeRequestFieldsUiState) {
                fieldsUiStateMutable.value = fieldsUiState
            }

            override fun cancelRequest(requestId: Int) {
                cancelRequestStateMutable.value = FetchResultUiState.Loading

                handle(
                    background = { repository.cancelRequest(requestId) }
                ) {
                    cancelRequestStateMutable.value = it.toUiState()
                }
            }

            override fun rejectDocument(requestId: Int, rejectReason: String) {
                rejectDocumentsStateMutable.value = FetchResultUiState.Loading
                handle(background = { repository.rejectDocument(requestId, rejectReason) }) {
                    rejectDocumentsStateMutable.value = it.toUiState()
                }
            }

            override fun approveDocument(requestId: Int) {
                approveDocumentsStateMutable.value = FetchResultUiState.Loading
                handle(background = { repository.approveDocument(requestId) }) {
                    approveDocumentsStateMutable.value = it.toUiState()
                }
            }
        }
        event.apply(action = action)
    }
}