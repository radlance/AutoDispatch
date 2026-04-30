package com.github.radlance.autodispatch.request.change.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.change.domain.ChangeRequestRepository
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
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
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val changeRequestState = changeRequestStateMutable.asStateFlow()

    private val cancelRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val cancelRequestState = cancelRequestStateMutable.asStateFlow()

    private val removeRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(
            FetchResultUiState.Idle
        )
    val removeRequestState = removeRequestStateMutable.asStateFlow()

    private val rejectDocumentsStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val rejectDocumentsState get() = rejectDocumentsStateMutable.asStateFlow()

    private val approveDocumentsStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val approveDocumentsState get() = approveDocumentsStateMutable.asStateFlow()

    private val driverUnassignmentStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(
            FetchResultUiState.Idle
        )
    val driverUnassignmentState = driverUnassignmentStateMutable.asStateFlow()

    override fun reduce(event: ChangeRequestEvent) {
        val action = object : CreateRequestAction {
            override fun changeDepartureCity(city: City) {
                fieldsUiStateMutable.update { state ->
                    state.copy(departureCity = city, departureCityError = false)
                }
            }

            override fun changeDestinationCity(city: City) {
                fieldsUiStateMutable.update { state ->
                    state.copy(destinationCity = city, destinationCityError = false)
                }
            }

            override fun changeCargoType(cargoType: CargoType) {
                fieldsUiStateMutable.update { state ->
                    state.copy(cargoType = cargoType, cargoTypeError = false)
                }
            }

            override fun changeCompanyName(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(companyNameFieldValue = value, companyNameError = false)
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
                        loadingFieldLonValue = value?.lon,
                        loadingPointError = false
                    )
                }
            }

            override fun changeUnloading(value: Point?) {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        unloadingFieldAddressValue = value?.toStringAddress() ?: "",
                        unloadingFieldLatValue = value?.lat,
                        unloadingFieldLonValue = value?.lon,
                        unloadingPointError = false
                    )
                }
            }

            override fun changePlannedLoadingAt(value: String) {
                val newLoad = DateTimeCoordinator.parse(value) ?: return

                fieldsUiStateMutable.update { state ->

                    val (load, unload) = DateTimeCoordinator.updateLoad(
                        currentUnload = state.plannedUnloadingAt,
                        newLoad = newLoad,
                        isEditing = state.requestId != null
                    )

                    state.copy(
                        plannedLoadingAt = load,
                        plannedUnloadingAt = unload
                    )
                }
            }

            override fun changePlannedUnloadingAt(value: String) {
                val newUnload = DateTimeCoordinator.parse(value) ?: return

                fieldsUiStateMutable.update { state ->

                    val (load, unload) = DateTimeCoordinator.updateUnload(
                        currentLoad = state.plannedLoadingAt,
                        newUnload = newUnload,
                        isEditing = state.requestId != null
                    )

                    state.copy(
                        plannedLoadingAt = load,
                        plannedUnloadingAt = unload
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
                requestId: Int?,
                plannedLoadingAt: String,
                plannedUnloadingAt: String
            ) {
                validateFields(requestId != null)

                with(fieldsUiStateMutable.value) {
                    if (
                        !departureCityError && !destinationCityError && !cargoTypeError
                        && !companyNameError && companyEmailErrorMessage.isEmpty()
                        && companyPhoneErrorMessage.isEmpty() && cargoWeightErrorMessage.isEmpty()
                        && cargoVolumeErrorMessage.isEmpty() && !loadingPointError
                        && !unloadingPointError
                        && !plannedLoadingError
                        && !plannedUnloadingError
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
                                    transportationDescription = additionalInfo,
                                    plannedLoadingAt = plannedLoadingAt,
                                    plannedUnloadingAt = plannedUnloadingAt
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
                        departureCityError = false,
                        destinationCity = null,
                        destinationCityError = false,
                        cargoType = null,
                        cargoTypeError = false,
                        companyNameFieldValue = "",
                        companyNameError = false,
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
                        loadingPointError = false,
                        unloadingFieldAddressValue = "",
                        unloadingFieldLatValue = null,
                        unloadingFieldLonValue = null,
                        unloadingPointError = false,
                        additionalInfoFieldValue = "",
                        plannedLoadingAt = "",
                        plannedUnloadingAt = ""
                    )
                }

                changeRequestStateMutable.value = FetchResultUiState.Idle
                customersStateMutable.value = emptyList()
                viewModelScope.coroutineContext.cancelChildren()
            }

            override fun resetCancelState() {
                cancelRequestStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetRemoveState() {
                removeRequestStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetRejectState() {
                rejectDocumentsStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetApproveState() {
                approveDocumentsStateMutable.value = FetchResultUiState.Idle
            }

            override fun resetDriverUnassignmentState() {
                driverUnassignmentStateMutable.value = FetchResultUiState.Idle
            }

            override fun setupRequestFieldsState(fieldsUiState: ChangeRequestFieldsUiState) {
                fieldsUiStateMutable.value = fieldsUiState
            }

            override fun validateFields(isEditing: Boolean) {
                fieldsUiStateMutable.update { state ->

                    val dateError = !DateTimeCoordinator.isValid(
                        state.plannedLoadingAt,
                        state.plannedUnloadingAt,
                        isEditing = isEditing
                    )

                    state.copy(
                        departureCityError = state.departureCity == null,
                        destinationCityError = state.destinationCity == null,
                        cargoTypeError = state.cargoType == null,
                        companyNameError = state.companyNameFieldValue.isBlank(),
                        companyEmailErrorMessage = validator.validationEmailMessage(state.companyEmailFieldValue),
                        companyPhoneErrorMessage = validator.validationPhoneNumberMessage(state.companyPhoneFieldValue),
                        cargoWeightErrorMessage = validator.validationWeightMessage(state.cargoWeightFieldValue),
                        cargoVolumeErrorMessage = state.cargoVolumeFieldValue.takeIf { it.isNotBlank() }
                            ?.let { validator.validationVolumeMessage(it) } ?: "",
                        loadingPointError = state.loadingFieldLatValue == null,
                        unloadingPointError = state.unloadingFieldLatValue == null,
                        plannedLoadingError = dateError,
                        plannedUnloadingError = dateError
                    )
                }
            }

            override fun cancelRequest(requestId: Int) {
                cancelRequestStateMutable.value = FetchResultUiState.Loading

                handle(
                    background = { repository.cancelRequest(requestId) }
                ) {
                    cancelRequestStateMutable.value = it.toUiState()
                }
            }

            override fun removeRequest(requestId: Int) {
                removeRequestStateMutable.value = FetchResultUiState.Loading

                handle(
                    background = { repository.removeRequest(requestId) }
                ) {
                    removeRequestStateMutable.value = it.toUiState()
                }
            }

            override fun rejectDocument(requestId: Int, rejectReason: String) {
                rejectDocumentsStateMutable.value = FetchResultUiState.Loading
                handle(background = { repository.rejectDocument(requestId, rejectReason) }) {
                    rejectDocumentsStateMutable.value = it.toUiState()
                }
            }

            override fun approveDocument(requestId: Int, isShipping: Boolean) {
                approveDocumentsStateMutable.value = FetchResultUiState.Loading

                handle(background = {
                    if (isShipping) {
                        repository.approveShippingDocument(requestId)
                    } else {
                        repository.approveDocument(requestId)
                    }
                }) {
                    approveDocumentsStateMutable.value = it.toUiState()
                }
            }

            override fun unassignDriver(requestId: Int) {
                driverUnassignmentStateMutable.value = FetchResultUiState.Loading
                handle(background = { repository.unassignDriver(requestId) }) {
                    driverUnassignmentStateMutable.value = it.toUiState()
                }
            }
        }
        event.apply(action = action)
    }
}
