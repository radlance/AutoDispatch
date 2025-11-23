package com.github.radlance.autodispatch.request.change.presentation

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Point

interface ChangeRequestEvent : Event {

    fun apply(action: CreateRequestAction)

    class ChangeDepartureCity(private val city: City) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeDepartureCity(city)
    }


    class ChangeDestinationCity(private val city: City) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeDestinationCity(city)
    }

    class ChangeCargoType(private val cargoType: CargoType) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoType(cargoType)
    }


    class ChangeCompanyName(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyName(value)
    }

    class ChangeCompanyEmail(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyEmail(value)
    }

    class ChangeCompanyPhone(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyPhone(value)
    }

    class ChangeWeight(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoWeight(value)
    }

    class ChangeVolume(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changCargoVolume(value)
    }

    class ChangeCargoDescription(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoDescription(value)
    }

    class ChangeLoading(private val value: Point?) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeLoading(value)
    }

    class ChangeUnloading(private val value: Point?) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeUnloading(value)
    }

    class ChangeAdditionalInfo(private val value: String) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeAdditionalInfo(value)
    }

    class ClickCreate(
        private val originId: Int,
        private val destinationId: Int,
        private val companyName: String,
        private val companyEmail: String,
        private val companyPhone: String,
        private val cargoTypeId: Int,
        private val cargoWeight: String,
        private val cargoVolume: String,
        private val cargoDescription: String,
        private val cargoLoadingAddress: String,
        private val cargoLoadingLat: Double,
        private val cargoLoadingLon: Double,
        private val cargoUnloadingAddress: String,
        private val cargoUnloadingLat: Double,
        private val cargoUnloadingLon: Double,
        private val additionalInfo: String,
        private val requestId: Int? = null
    ) : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) = action.createRequest(
            originId = originId,
            destinationId = destinationId,
            companyName = companyName,
            companyEmail = companyEmail,
            companyPhone = if (companyPhone.isBlank()) null else "+7$companyPhone",
            cargoTypeId = cargoTypeId,
            cargoWeight = cargoWeight,
            cargoVolume = cargoVolume.ifBlank { null },
            cargoDescription = cargoDescription.ifBlank { null },
            cargoLoadingAddress = cargoLoadingAddress.ifBlank { null },
            cargoLoadingLat = cargoLoadingLat,
            cargoLoadingLon = cargoLoadingLon,
            cargoUnloadingAddress = cargoUnloadingAddress.ifBlank { null },
            cargoUnloadingLat = cargoUnloadingLat,
            cargoUnloadingLon = cargoUnloadingLon,
            additionalInfo = additionalInfo.ifBlank { null },
            requestId = requestId
        )
    }

    class ClickCancelRequest(private val requestId: Int): ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) {
            action.cancelRequest(requestId)
        }
    }

    class ClickCancelAssignment(private val requestId: Int): ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) {
            action.cancelAssignment(requestId)
        }
    }

    object ResetChangeState : ChangeRequestEvent {
        override fun apply(action: CreateRequestAction) {
            action.resetChangeState()
        }
    }

    object ResetCancelState : ChangeRequestEvent {

        override fun apply(action: CreateRequestAction) {
            action.resetRemoveState()
        }
    }

    class SetupFieldsState(private val fieldsUiState: ChangeRequestFieldsUiState) :
        ChangeRequestEvent {
        override fun apply(action: CreateRequestAction) {
            action.setupRequestFieldsState(fieldsUiState)
        }
    }
}

interface CreateRequestAction {

    fun changeDepartureCity(city: City)

    fun changeDestinationCity(city: City)

    fun changeCargoType(cargoType: CargoType)

    fun changeCompanyName(value: String)

    fun changeCompanyEmail(value: String)

    fun changeCompanyPhone(value: String)

    fun changeCargoWeight(value: String)

    fun changCargoVolume(value: String)

    fun changeCargoDescription(value: String)

    fun changeLoading(value: Point?)

    fun changeUnloading(value: Point?)

    fun changeAdditionalInfo(value: String)

    fun createRequest(
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
    )

    fun cancelRequest(requestId: Int)

    fun cancelAssignment(requestId: Int)

    fun resetChangeState()

    fun resetRemoveState()

    fun setupRequestFieldsState(fieldsUiState: ChangeRequestFieldsUiState)
}