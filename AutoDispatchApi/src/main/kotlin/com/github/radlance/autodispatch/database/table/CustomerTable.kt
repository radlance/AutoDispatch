package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object CustomerTable : IntIdTable(name = "customer") {
    val organizationName = varchar(name = "organization_name", length = 100)
    val phoneNumber = varchar(name = "phone_number", length = 20)
    val email = varchar(name = "email", length = 254)
}