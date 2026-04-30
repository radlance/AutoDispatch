package com.github.radlance.autodispatch.request.core.domain

enum class DocumentType(
    val id: Int,
    val title: String
) {
    SHIPPING(1, "Отгрузка"),
    ACCEPTANCE(2, "Назначение");

    companion object {
        fun fromId(id: Int): DocumentType? =
            DocumentType.entries.firstOrNull { it.id == id }
    }
}

fun Int.toDocumentType(): DocumentType =
    DocumentType.fromId(this) ?: error("Unknown document type id=$this")