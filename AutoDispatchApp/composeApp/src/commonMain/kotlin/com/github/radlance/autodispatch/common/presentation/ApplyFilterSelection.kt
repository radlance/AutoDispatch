package com.github.radlance.autodispatch.common.presentation

inline fun <T> applyFilterSelection(
    selectedNames: List<String>,
    allItems: List<T>,
    crossinline nameSelector: (T) -> String,
    crossinline idSelector: (T) -> Int
): List<Int> {
    if (selectedNames.isEmpty()) return emptyList()

    val selectedIds = allItems.filter { nameSelector(it) in selectedNames }.map(idSelector)
    return if (selectedIds.size == allItems.size) emptyList() else selectedIds
}