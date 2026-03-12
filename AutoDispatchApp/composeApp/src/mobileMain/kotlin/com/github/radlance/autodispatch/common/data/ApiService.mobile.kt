package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.details.data.DeliveryDetailedDto
import com.github.radlance.autodispatch.profile.data.ProfileDetailsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.content.PartData

interface ApiServiceMobile : ApiService {

    suspend fun deliveries(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DeliveryDto>

    suspend fun deliveryDetails(deliveryId: Int): DeliveryDetailedDto

    suspend fun startDelivery(deliveryId: Int)

    suspend fun arriveLoading(deliveryId: Int)

    suspend fun departLoading(deliveryId: Int)

    suspend fun arriveUnloading(deliveryId: Int)

    suspend fun completeDelivery(deliveryId: Int, formData: List<PartData>)

    suspend fun retakeDocument(deliveryId: Int, formData: List<PartData>)

    suspend fun history(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DeliveryDto>

    suspend fun profileDetails(): ProfileDetailsDto

    suspend fun uploadProfileImage(formData: List<PartData>)

    suspend fun removeProfileImage()
}

internal class KtorApiServiceMobile(
    private val httpClient: HttpClient,
    private val apiService: ApiService
) : ApiServiceMobile, ApiService by apiService {

    override suspend fun deliveries(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DeliveryDto> {

        return httpClient.get("deliveries") {
            searchQuery?.let { parameter("search", it) }
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
        }.body()
    }

    override suspend fun deliveryDetails(deliveryId: Int): DeliveryDetailedDto {
        return httpClient.get("deliveries/${deliveryId}/details").body()
    }

    override suspend fun startDelivery(deliveryId: Int) {
        httpClient.post("deliveries/${deliveryId}/start")
    }

    override suspend fun arriveLoading(deliveryId: Int) {
        httpClient.post("deliveries/${deliveryId}/arrive-loading")
    }

    override suspend fun departLoading(deliveryId: Int) {
        httpClient.post("deliveries/${deliveryId}/depart-loading")
    }

    override suspend fun arriveUnloading(deliveryId: Int) {
        httpClient.post("deliveries/${deliveryId}/arrive-unloading")
    }

    override suspend fun completeDelivery(
        deliveryId: Int,
        formData: List<PartData>
    ) {

        httpClient.submitFormWithBinaryData(
            url = "deliveries/$deliveryId/upload-documents",
            formData = formData
        )
    }

    override suspend fun retakeDocument(
        deliveryId: Int,
        formData: List<PartData>
    ) {
        httpClient.submitFormWithBinaryData(
            url = "deliveries/$deliveryId/retake-documents",
            formData = formData
        )
    }

    override suspend fun history(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DeliveryDto> {

        return httpClient.get("deliveries/history/my") {
            searchQuery?.let { parameter("search", it) }
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
        }.body()
    }

    override suspend fun profileDetails(): ProfileDetailsDto {
        return httpClient.get("profile/details").body()
    }

    override suspend fun uploadProfileImage(formData: List<PartData>) {
        httpClient.submitFormWithBinaryData(
            url = "profile/avatar",
            formData = formData
        )
    }

    override suspend fun removeProfileImage() {
        httpClient.delete("profile/avatar")
    }
}
