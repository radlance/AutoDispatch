package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.additional_info
import autodispatch.composeapp.generated.resources.cargo_info
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.creation_date
import autodispatch.composeapp.generated.resources.customer
import autodispatch.composeapp.generated.resources.customer_email
import autodispatch.composeapp.generated.resources.customer_phone
import autodispatch.composeapp.generated.resources.description
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.driver_and_vehicle
import autodispatch.composeapp.generated.resources.loading_point
import autodispatch.composeapp.generated.resources.loading_unloading_points
import autodispatch.composeapp.generated.resources.request_creation_date
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import autodispatch.composeapp.generated.resources.unloading_point
import autodispatch.composeapp.generated.resources.vehicle
import autodispatch.composeapp.generated.resources.volume
import autodispatch.composeapp.generated.resources.weight
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.LoadableImage
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.common.presentation.StatusWithColor
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.common.utils.formattedLicensePlate
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.presentation.ProgressStep
import com.github.radlance.autodispatch.delivery.presentation.ProgressWithAnimationByButton
import com.github.radlance.autodispatch.request.core.domain.Request
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestDetailsSections(
    scrollState: ScrollState,
    lazyRowState: LazyListState,
    request: Request,
    lastImageRetryAttempt: Long,
    onReloadImage: (Long) -> Unit,
    onSelectImageUrl: (String) -> Unit,
    isReassign: Boolean,
    onChangeReassign: (Boolean) -> Unit,
    showDriverAssignmentDialog: () -> Unit,
    showRejectDocumentsDialog: () -> Unit,
    showApproveDocumentsDialog: () -> Unit,
    showDriverUnassignmentDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressSteps = remember(request) {
        with(request) {
            listOf(
                arrivedLoadingAt,
                actualLoadingAt,
                arrivedUnloadingAt,
                actualUnloadingAt
            ).map { time ->
                ProgressStep(
                    label = "",
                    time = time?.toSimpleDateWithTimeString() ?: "",
                    isCompleted = time != null
                )
            }
        }
    }

    val progressValue = remember(progressSteps) {
        progressSteps.count { it.isCompleted } / 4f
    }
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.height(SECTION_GAP))

        Section(header = stringResource(Res.string.status)) {
            StatusWithColor(
                status = request.status,
                fontSize = 14.sp
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = stringResource(Res.string.route)) {
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                iconDesc = stringResource(Res.string.route),
                text = routeText(request.origin, request.destination)
            )
            Spacer(modifier = Modifier.height(SECTION_GAP))
            ProgressWithAnimationByButton(
                progress = progressValue,
                leftLabel = "загрузка",
                rightLabel = "разгрузка",
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = stringResource(Res.string.request_creation_date)) {
            InfoRow(
                icon = Icons.Outlined.CalendarToday,
                iconDesc = stringResource(Res.string.creation_date),
                text = request.createdAt.toSimpleDateWithTimeString()
            )
        }

        Spacer(modifier = Modifier.height(SECTION_GAP))

        Section(header = stringResource(Res.string.cargo_info)) {
            LabeledValue(
                label = stringResource(Res.string.cargo_type),
                value = request.cargo.type.name
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = stringResource(Res.string.weight),
                value = request.cargo.weight.formatKg()
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = stringResource(Res.string.volume),
                value = request.cargo.volume?.formatM3()
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = stringResource(Res.string.description),
                value = request.cargo.description
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = stringResource(Res.string.loading_unloading_points)) {
            LabeledValue(
                label = stringResource(Res.string.loading_point),
                value = request.loadingPoint.toStringAddress()
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Плановая загрузка",
                value = request.plannedLoadingAt.toSimpleDateWithTimeString()
            )
            request.actualLoadingAt?.let {
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(
                    label = "Фактическая загрузка",
                    value = it.toSimpleDateWithTimeString()
                )
            }
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = stringResource(Res.string.unloading_point),
                value = request.unloadingPoint.toStringAddress()
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Плановая разгрузка",
                value = request.plannedUnloadingAt.toSimpleDateWithTimeString()
            )
            request.actualUnloadingAt?.let {
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(
                    label = "Фактическая разгрузка",
                    value = it.toSimpleDateWithTimeString()
                )
            }
            request.transportationDescription?.let {
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(
                    label = stringResource(Res.string.additional_info),
                    value = request.transportationDescription
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = stringResource(Res.string.customer)) {
            InfoRow(
                icon = Icons.Outlined.Person,
                iconDesc = stringResource(Res.string.customer),
                text = request.customer.organizationName
            )

            request.customer.phoneNumber.let {
                Spacer(modifier = Modifier.height(ITEM_GAP))
                InfoRow(
                    icon = Icons.Outlined.Phone,
                    iconDesc = stringResource(Res.string.customer_phone),
                    text = it
                )
            }

            Spacer(modifier = Modifier.height(ITEM_GAP))
            InfoRow(
                icon = Icons.Outlined.Mail,
                iconDesc = stringResource(Res.string.customer_email),
                text = request.customer.email
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = stringResource(Res.string.driver_and_vehicle)) {
            LabeledValue(
                label = stringResource(Res.string.driver),
                value = request.driverFullName
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            val vehicle =
                request.vehicle?.let { "${it.model} (${it.formattedLicensePlate()})" } ?: "—"
            LabeledValue(label = stringResource(Res.string.vehicle), value = vehicle)
            request.vehicle?.let {
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Грузоподъёмность", value = "${it.payloadCapacity} кг")
            }
        }
        if ((request.status == RequestStatus.OnCheck
                    || request.status == RequestStatus.Completed
                    || request.status == RequestStatus.Rejected
                    || request.status == RequestStatus.InProgress)
            && request.documents.isNotEmpty()
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(
                    top = SECTION_GAP,
                    bottom = SECTION_GAP,
                    end = 6.dp
                )
            )
            Section(header = "Документы от водителя") {

                val lastSent = request.documents.maxOf { it.uploadedAt }
                InfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    iconDesc = stringResource(Res.string.creation_date),
                    text = lastSent.toSimpleDateWithTimeString()
                )
                Spacer(modifier = Modifier.height(ITEM_GAP))

                Text(
                    text = "Фотографии документов:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LazyRow(
                    state = lazyRowState,
                    contentPadding = PaddingValues(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    itemsIndexed(
                        items = request.documents,
                        key = { idx, _ -> idx }
                    ) { _, document ->

                        LoadableImage(
                            documentUrl = document.imageUrl,
                            onRetry = {
                                onReloadImage(System.currentTimeMillis())
                            },
                            lastRetryAttempt = lastImageRetryAttempt,
                            onImageSelected = onSelectImageUrl
                        )
                    }
                }
                if (request.status == RequestStatus.OnCheck) {
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = showRejectDocumentsDialog,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(text = "Отклонить")
                        }
                        Spacer(Modifier.width(12.dp))
                        Button(
                            onClick = showApproveDocumentsDialog,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Done, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(text = "Одобрить")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (request.status == RequestStatus.Waiting || request.status == RequestStatus.Assigned) {
            onChangeReassign(request.status == RequestStatus.Assigned)
            Row {
                Button(
                    onClick = showDriverAssignmentDialog,
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    val text = if (isReassign) {
                        "Переназначить"
                    } else "Назначить"
                    Text(text = text)
                }

                if (isReassign) {
                    Button(
                        onClick = showDriverUnassignmentDialog,
                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                    ) {
                        Text(text = "Снять с заявки")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

fun routeText(origin: String, destination: String): String =
    listOf(origin, destination)
        .filter { it.isNotBlank() }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" → ")
        ?: "—"
