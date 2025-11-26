package com.github.radlance.autodispatch.platform

import com.github.radlance.autodispatch.delivery.route.domain.Location

expect suspend fun getCurrentLocation(context: Any?): Location?