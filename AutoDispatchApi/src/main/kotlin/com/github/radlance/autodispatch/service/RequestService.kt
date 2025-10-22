package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.request.RequestResponse
import com.github.radlance.autodispatch.repository.RequestRepository

class RequestService(
    private val requestRepository: RequestRepository
) {
    suspend fun requests(): RequestResponse {
        val requests = requestRepository.requests()
        return requests
    }
}