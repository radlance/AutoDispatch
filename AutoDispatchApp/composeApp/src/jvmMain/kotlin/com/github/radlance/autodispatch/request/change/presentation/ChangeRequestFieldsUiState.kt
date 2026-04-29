package com.github.radlance.autodispatch.request.change.presentation

import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City

data class ChangeRequestFieldsUiState(
    val departureCity: City? = null,
    val departureCityError: Boolean = false,
    val destinationCity: City? = null,
    val destinationCityError: Boolean = false,
    val cargoType: CargoType? = null,
    val cargoTypeError: Boolean = false,
    val requestId: Int? = null,
    val requestNumber: String = "",
    val companyNameFieldValue: String = "",
    val companyNameError: Boolean = false,
    val companyEmailFieldValue: String = "",
    val companyEmailErrorMessage: String = "",
    val companyPhoneFieldValue: String = "",
    val companyPhoneErrorMessage: String = "",
    val cargoWeightFieldValue: String = "",
    val cargoWeightErrorMessage: String = "",
    val cargoVolumeFieldValue: String = "",
    val cargoVolumeErrorMessage: String = "",
    val cargoDescriptionFieldValue: String = "",
    val loadingFieldAddressValue: String = "",
    val loadingFieldLatValue: Double? = null,
    val loadingFieldLonValue: Double? = null,
    val loadingPointError: Boolean = false,
    val unloadingFieldAddressValue: String = "",
    val unloadingFieldLatValue: Double? = null,
    val unloadingFieldLonValue: Double? = null,
    val unloadingPointError: Boolean = false,
    val additionalInfoFieldValue: String = "",
    val plannedLoadingAt: String = "",
    val plannedUnloadingAt: String = "",
    val plannedLoadingError: Boolean = false,
    val plannedUnloadingError: Boolean = false
)