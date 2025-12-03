package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.domain.document.RejectDocumentDto
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.update

class DocumentsRepository {

    suspend fun rejectDocument(requestId: Int, rejectDocumentDto: RejectDocumentDto) = loggedTransaction {
        RequestTable.update({ RequestTable.id eq requestId }) {
            it[statusId] = 7
            it[rejectionReason] = rejectDocumentDto.reason
        }
    }

    suspend fun approveDocument(requestId: Int) = loggedTransaction {
        RequestTable.update({ RequestTable.id eq requestId }) {
            it[statusId] = 4
        }
    }
}