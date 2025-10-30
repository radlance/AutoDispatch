package com.github.radlance.autodispatch.request.presentation.create

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City

interface CreateRequestEvent : Event {

    fun apply(action: CreateRequestAction)

    class ChangeDepartureCity(private val city: City) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeDepartureCity(city)
    }


    class ChangeDestinationCity(private val city: City) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeDestinationCity(city)
    }

    class ChangeCargoType(private val cargoType: CargoType) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoType(cargoType)
    }


    class ChangeCompanyName(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyName(value)
    }

    class ChangeCompanyEmail(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyEmail(value)
    }

    class ChangeCompanyPhone(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCompanyPhone(value)
    }

    class ChangeWeight(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoWeight(value)
    }

    class ChangeVolume(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changCargoVolume(value)
    }

    class ChangeCargoDescription(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeCargoDescription(value)
    }

    class ChangeLoading(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeLoading(value)
    }

    class ChangeUnloading(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeUnloading(value)
    }

    class ChangeAdditionalInfo(private val value: String) : CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.changeAdditionalInfo(value)
    }

    class ClickCreate(
        private val companyName: String,
        private val companyEmail: String,
        private val companyPhone: String?,
        private val cargoWeight: String,
        private val cargoVolume: String?,
        private val cargoDescription: String?,
        private val cargoLoading: String,
        private val cargoUnloading: String,
        private val additionalInfo: String?
    ) :
        CreateRequestEvent {

        override fun apply(action: CreateRequestAction) = action.createRequest(
            companyName = companyName,
            companyEmail = companyEmail,
            companyPhone = companyPhone,
            cargoWeight = cargoWeight,
            cargoVolume = cargoVolume,
            cargoDescription = cargoDescription,
            cargoLoading = cargoLoading,
            cargoUnloading = cargoUnloading,
            additionalInfo = additionalInfo
        )
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

    fun changeLoading(value: String)

    fun changeUnloading(value: String)

    fun changeAdditionalInfo(value: String)

    fun createRequest(
        companyName: String,
        companyEmail: String,
        companyPhone: String?,
        cargoWeight: String,
        cargoVolume: String?,
        cargoDescription: String?,
        cargoLoading: String,
        cargoUnloading: String,
        additionalInfo: String?
    )
}