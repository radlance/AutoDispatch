package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.CustomerTable
import com.github.radlance.autodispatch.domain.request.Customer
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CustomerEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<CustomerEntity>(CustomerTable)

    val organizationName by CustomerTable.organizationName
    val phoneNumber by CustomerTable.phoneNumber
    val email by CustomerTable.email

    fun toCustomer(): Customer {
        return Customer(
            id = id.value,
            organizationName = organizationName,
            email = email,
            phoneNumber = phoneNumber
        )
    }
}