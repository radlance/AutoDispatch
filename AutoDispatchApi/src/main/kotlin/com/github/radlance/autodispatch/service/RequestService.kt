package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.domain.request.Customer
import com.github.radlance.autodispatch.domain.request.DriverStats
import com.github.radlance.autodispatch.domain.request.Filters
import com.github.radlance.autodispatch.domain.request.PaginatedResult
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.repository.RequestRepository

class RequestService(
    private val requestRepository: RequestRepository,
    private val profileRepository: ProfileRepository
) {
    suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): PaginatedResult<Request> {

        val requests = requestRepository.requests(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery,
            originCityIds = originCityIds,
            destinationCityIds = destinationCityIds,
            cargoTypeIds = cargoTypeIds,
            statusIds = statusIds,
            driverIds = driverIds,
            vehicleIds = vehicleIds
        )
        return requests
    }

    suspend fun filters(): Filters {
        val filters = requestRepository.filters()
        return filters
    }

    suspend fun createRequest(login: String, request: CreateRequest) {
        val currentUser = profileRepository.userByLogin(login)
        requestRepository.createRequest(createdById = currentUser.id, createRequest = request)
    }

    suspend fun customers(query: String): List<Customer> {
        val customers = requestRepository.customers(query)
        return customers
    }

    suspend fun editRequest(login: String, requestId: Int, request: CreateRequest) {
        val currentUser = profileRepository.userByLogin(login)
        requestRepository.editRequest(createdById = currentUser.id, requestId = requestId, editRequest = request)
    }

    suspend fun removeRequest(requestId: Int) {
        requestRepository.removeRequest(requestId)
    }

    suspend fun requestAssignment(): List<DriverStats> {
        return requestRepository.requestAssignment()
    }

    suspend fun assignRequestToDriver(requestId: Int, driverId: Int) {
        requestRepository.assignRequestToDriver(
            requestId = requestId,
            driverId = driverId
        )
    }

    suspend fun reAssignRequestToDriver(requestId: Int, driverId: Int) {
        requestRepository.reassignRequestToDriver(
            requestId = requestId,
            driverId = driverId
        )
    }
}