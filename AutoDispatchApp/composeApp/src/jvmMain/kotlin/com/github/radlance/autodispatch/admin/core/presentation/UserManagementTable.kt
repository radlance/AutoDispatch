package com.github.radlance.autodispatch.admin.core.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.utils.toSimpleDateString
import com.github.radlance.autodispatch.request.core.presentation.CustomPaginationDataTable
import com.github.radlance.autodispatch.uikit.theme.statusPalette
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserManagementTable(
    users: List<UserDetailed>,
    selectedUser: UserDetailed?,
    showPanel: Boolean,
    onUserClick: (UserDetailed) -> Unit,
    dataTableState: DataTableState,
    pageIndex: Int,
    pageSize: Int,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(users.size) {
        dataTableState.verticalScrollState.scrollTo(0)
    }

    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val animatedColors: Map<UserDetailed, Color> = users.associateWith { user ->
        val target = if (user == selectedUser && showPanel) highlight else Color.Transparent
        animateColorAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 200),
            label = "rowColorAnimation_${user.id}"
        ).value
    }

    CustomPaginationDataTable(
        modifier = modifier,
        dataTableState = dataTableState,
        columns = listOf(
            DataColumn(width = TableColumnWidth.Flex(0.2f)) {
                Text("№")
            },
            DataColumn(width = TableColumnWidth.Flex(1f)) {
                Text("Логин")
            },
            DataColumn(width = TableColumnWidth.Flex(2f)) {
                Text("ФИО")
            },
            DataColumn(width = TableColumnWidth.Flex(1f)) {
                Text("Email")
            },
            DataColumn(width = TableColumnWidth.Flex(1.2f)) {
                Text("Телефон")
            },
            DataColumn(width = TableColumnWidth.Flex(1f)) {
                Text("Роль")
            },
            DataColumn(width = TableColumnWidth.Flex(1.2f)) {
                Text(stringResource(Res.string.status))
            },
            DataColumn(width = TableColumnWidth.Flex(1f)) {
                Text("Создан")
            },
            DataColumn(width = TableColumnWidth.Flex(1f)) {
                Text("Последний вход")
            },
        )
    ) {
        users.forEachIndexed { index, item ->
            row {
                backgroundColor = animatedColors[item] ?: Color.Transparent
                onClick = {
                    onUserClick(item)
                    scope.launch {
                        dataTableState.horizontalScrollState.scrollTo(0)
                    }
                }
                cell {
                    Text(
                        text = (pageIndex * pageSize + index + 1).toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.login,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.fullName,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.email,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.phoneNumber,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.role.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    UserStatusBadge(
                        status = item.status
                    )
                }
                cell {
                    Text(
                        text = item.createdAt.toSimpleDateString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.lastLoginAt?.toSimpleDateString() ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun UserStatusBadge(status: UserStatus) {
    val palette = MaterialTheme.statusPalette
    val (bgColor, textColor) = when (status) {
        UserStatus.Active -> palette.successBg to palette.successText
        UserStatus.Blocked -> palette.errorBg to palette.errorText
        UserStatus.Deleted -> palette.neutralBg to palette.neutralText
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.title,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
