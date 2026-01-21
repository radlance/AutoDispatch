package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.createImageFormData
import com.github.radlance.autodispatch.common.data.toProfileDetails
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.core.data.DeliveryCache
import com.github.radlance.autodispatch.profile.domain.DeliveriesStats
import com.github.radlance.autodispatch.profile.domain.DriverProfileRepository
import com.github.radlance.autodispatch.profile.domain.ProfileDetails
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteDriverProfileRepository(
    private val profileRepository: ProfileRepository,
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest,
    private val deliveryCache: DeliveryCache
) : DriverProfileRepository, ProfileRepository by profileRepository {

    override suspend fun profileDetails(): FetchResult<ProfileDetails, String> =
        handleRequest.handle {
            apiService.profileDetails().toProfileDetails()
        }

    override fun deliveriesStatsFlow(): Flow<DeliveriesStats> =
        deliveryCache.items.map { items ->
            val deliveries = items.values

            DeliveriesStats(
                totalCount = deliveries.size,
                activeCount = deliveries.count { it.status.id in listOf(2, 3) },
                completedCount = deliveries.count { it.status.id == 4 },
                canceledCount = deliveries.count { it.status.id == 5 },
                onCheckCount = deliveries.count { it.status.id == 6 },
                rejectedCount = deliveries.count { it.status.id == 7 }
            )
        }


    override suspend fun uploadProfileImage(image: ByteArray): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.uploadProfileImage(image.createImageFormData())
        }

    override suspend fun deleteProfileImage(): FetchResult<Unit, String> = handleRequest.handle {
        apiService.removeProfileImage()
    }
}