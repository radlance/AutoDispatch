package com.github.radlance.autodispatch.admin.core.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserDetailsSection(
    scrollState: ScrollState,
    user: UserDetailed,
    onBlockUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(SECTION_GAP))
        Section(header = stringResource(Res.string.status)) {
            UserStatusBadge(
                status = user.status
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = "Пользователь") {
            LabeledValue(
                label = "ФИО",
                value = user.fullName
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Логин",
                value = user.login
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Электронная почта",
                value = user.email
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Телефон",
                value = user.phoneNumber
            )
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Роль",
                value = user.role.title
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        Section(header = "Активность") {
            LabeledValue(
                label = "Дата регистрации",
                value = user.createdAt.toSimpleDateWithTimeString()
            )
            user.createdBy?.let {
                Section(header = "Кем создан") {
                    LabeledValue(
                        label = "ФИО",
                        value = user.createdBy.fullName
                    )
                    LabeledValue(
                        label = "Логин",
                        value = user.createdBy.login
                    )
                    LabeledValue(
                        label = "Номер телефона",
                        value = user.createdBy.phoneNumber
                    )
                }
            }
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Дата изменения",
                value = user.updatedAt?.toSimpleDateWithTimeString() ?: "—"
            )
            user.updatedBy?.let {
                Section(header = "Кем обновлён") {
                    LabeledValue(
                        label = "ФИО",
                        value = user.updatedBy.fullName
                    )
                    LabeledValue(
                        label = "Логин",
                        value = user.updatedBy.login
                    )
                    LabeledValue(
                        label = "Номер телефона",
                        value = user.updatedBy.phoneNumber
                    )
                }
            }
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(
                label = "Последний вход",
                value = user.lastLoginAt?.toSimpleDateWithTimeString() ?: "—"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Button(
                onClick = onBlockUser,
                modifier = Modifier.weight(1f).padding(end = 6.dp)
            ) {
                val text = if (user.status == UserStatus.Blocked) {
                    "Разблокировать"
                } else "Заблокировать"

                Text(text = text)
            }

            Button(
                onClick = {},
                modifier = Modifier.weight(1f).padding(end = 6.dp)
            ) {
                Text(text = "Удалить")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}