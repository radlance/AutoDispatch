package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object DeliveryDocumentTable : IntIdTable("delivery_document") {
    val assignmentId = reference("assignment_id", AssignmentTable)
    val imageUrl = varchar("image_url", 255)
    val uploadedAt = timestampWithTimeZone("uploaded_at").nullable()
    val typeId = reference("type_id", DeliveryDocumentTypeTable)
}