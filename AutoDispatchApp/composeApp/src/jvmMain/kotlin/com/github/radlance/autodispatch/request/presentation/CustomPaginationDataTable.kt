package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.material3.Material3CellContentProvider
import com.seanproctor.datatable.paging.PaginatedDataTableState
import kotlin.math.min

@Composable
fun CustomPaginationDataTable(
    columns: List<DataColumn>,
    state: PaginatedDataTableState,
    modifier: Modifier = Modifier,
    dataTableState: DataTableState = remember(state.pageSize, state.pageIndex) { DataTableState() },
    separator: @Composable () -> Unit = { HorizontalDivider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    footerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(end = 16.dp)) {
        CustomPaginatedDataTable(
            dataTableState = dataTableState,
            columns = columns,
            modifier = Modifier.fillMaxWidth(),
            separator = separator,
            headerHeight = headerHeight,
            rowHeight = rowHeight,
            contentPadding = contentPadding,
            headerBackgroundColor = headerBackgroundColor,
            footerBackgroundColor = footerBackgroundColor,
            state = state,
            footer = {},
            cellContentProvider = Material3CellContentProvider,
            sortColumnIndex = sortColumnIndex,
            sortAscending = sortAscending,
            logger = logger,
            content = content
        )

        Column {
            HorizontalScrollbar(
                modifier = Modifier
                    .fillMaxWidth(),
                adapter = rememberDataTableScrollbarAdapter(
                    scrollState = dataTableState.horizontalScrollState
                )
            )
            Row(
                modifier = Modifier
                    .height(rowHeight)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(footerBackgroundColor),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val start = min(state.pageIndex * state.pageSize + 1, state.count)
                val end = min(start + state.pageSize - 1, state.count)
                val pageCount = (state.count + state.pageSize - 1) / state.pageSize
                Text("$start-$end из ${state.count}")
                IconButton(
                    onClick = { state.pageIndex = 0 },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FirstPage,
                        contentDescription = "First page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex-- },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex++ },
                    enabled = state.pageIndex < pageCount - 1
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex = pageCount - 1 },
                    enabled = state.pageIndex < pageCount - 1
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.LastPage,
                        contentDescription = "Last page",
                    )
                }
            }
        }
    }
}