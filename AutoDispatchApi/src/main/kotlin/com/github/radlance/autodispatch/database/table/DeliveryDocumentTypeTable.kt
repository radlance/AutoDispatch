package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object DeliveryDocumentTypeTable : IntIdTable("delivery_document_type") {
    val name = varchar(name = "name", length = 100)
}