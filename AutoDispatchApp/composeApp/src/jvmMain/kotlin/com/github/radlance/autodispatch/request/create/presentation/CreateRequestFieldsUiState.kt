package com.github.radlance.autodispatch.request.create.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City

data class CreateRequestFieldsUiState(
    val departureCity: City? = null,
    val destinationCity: City? = null,
    val cargoType: CargoType? = null,
    val companyNameFieldValue: TextFieldValue = TextFieldValue(""),
    val companyEmailFieldValue: String = "",
    val companyEmailErrorMessage: String = "",
    val companyPhoneFieldValue: String = "",
    val companyPhoneErrorMessage: String = "",
    val cargoWeightFieldValue: String = "",
    val cargoWeightErrorMessage: String = "",
    val cargoVolumeFieldValue: String = "",
    val cargoVolumeErrorMessage: String = "",
    val cargoDescriptionFieldValue: String = "",
    val loadingFieldValue: String = "",
    val unloadingFieldValue: String = "",
    val additionalInfoFieldValue: String = "",
)