package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.CellContentProvider
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.DefaultCellContentProvider
import com.seanproctor.datatable.paging.PaginatedDataTableState

@Composable
fun CustomPaginatedDataTable(
    columns: List<DataColumn>,
    state: PaginatedDataTableState,
    modifier: Modifier = Modifier,
    dataTableState: DataTableState = remember(state.pageSize, state.pageIndex) { DataTableState() },
    separator: @Composable () -> Unit = { },
    headerHeight: Dp = Dp.Unspecified,
    rowHeight: Dp = Dp.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    footer: @Composable () -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    BasicDataTable(
        columns = columns,
        modifier = modifier,
        state = dataTableState,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        headerBackgroundColor = headerBackgroundColor,
        footerBackgroundColor = footerBackgroundColor,
        footer = footer,
        cellContentProvider = cellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        logger = logger,
    ) {
        val start = state.pageIndex * state.pageSize
        val scope = CustomPaginatedRowScope(start, start + state.pageSize, this)
        content(scope)
        if (state.count != scope.index) {
            state.count = scope.index
        }
    }
}
