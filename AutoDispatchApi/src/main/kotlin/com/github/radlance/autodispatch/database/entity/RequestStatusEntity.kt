package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.domain.request.RequestStatus
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RequestStatusEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<RequestStatusEntity>(RequestStatusTable)

    val name by RequestStatusTable.name

    fun toRequestStatus(): RequestStatus {
        return RequestStatus(id = id.value, name = name)
    }
}