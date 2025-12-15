package com.github.radlance.autodispatch.request.change.presentation

import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City

data class ChangeRequestFieldsUiState(
    val departureCity: City? = null,
    val destinationCity: City? = null,
    val cargoType: CargoType? = null,
    val requestId: Int? = null,
    val requestNumber: String = "",
    val companyNameFieldValue: String = "",
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
    val unloadingFieldAddressValue: String = "",
    val unloadingFieldLatValue: Double? = null,
    val unloadingFieldLonValue: Double? = null,
    val additionalInfoFieldValue: String = "",
)